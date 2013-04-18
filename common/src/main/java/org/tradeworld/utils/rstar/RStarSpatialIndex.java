package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;
import org.tradeworld.utils.bbox.MutableBoundingBox;

import java.util.*;

/**
 * R*-Tree based spatial index.
 */
public class RStarSpatialIndex<T> extends SpatialIndexBase<T> {

    private final int maxFill = 64;

    private final int minFill = (int) (maxFill * 0.4);
    private Node<T> rootNode = new Node<T>();

    private static final Comparator<Node> NODE_X_COMPARATOR = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            double o1Min = o1.getBounds().getMinX();
            double o2Min = o2.getBounds().getMinX();

            // Sort primarily by min coordinate
            if (o1Min < o2Min) return -1;
            else if (o1Min > o2Min) return 1;
            else {
                double o1Max = o1.getBounds().getMaxX();
                double o2Max = o2.getBounds().getMaxX();

                // Secondary by max coordinate
                if (o1Max < o2Max) return -1;
                else if (o1Max > o2Max) return 1;
                else return 0;
            }
        }
    };

    private static final Comparator<Node> NODE_Y_COMPARATOR = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            double o1Min = o1.getBounds().getMinY();
            double o2Min = o2.getBounds().getMinY();

            // Sort primarily by min coordinate
            if (o1Min < o2Min) return -1;
            else if (o1Min > o2Min) return 1;
            else {
                double o1Max = o1.getBounds().getMaxY();
                double o2Max = o2.getBounds().getMaxY();

                // Secondary by max coordinate
                if (o1Max < o2Max) return -1;
                else if (o1Max > o2Max) return 1;
                else return 0;
            }
        }
    };

    @Override
    protected void rawAdd(T object, BoundingBox boundingBox) {
        // Create leaf node for the object
        Node<T> entry = new Node<T>(object, boundingBox);

        // Determine where to add
        Node<T> nodeToAddObjectTo = chooseSubtree(rootNode, boundingBox);

        // Add
        nodeToAddObjectTo.addChildNode(entry);

        // If the chosen subtree got filled to max capacity, split it
        if (nodeToAddObjectTo.getSize() > maxFill) {
            nodeToAddObjectTo.split();
            splitNode(nodeToAddObjectTo);
        }

        // TODO
    }

    @Override
    protected void rawRemove(T object) {
        final BoundingBox boundingBox = getBoundingBox(object);
        if (boundingBox != null) {
            // Find node with the object

            // Remove object from node

            // If node has less than min capacity, merge

        }

        // TODO
    }

    @Override
    protected int rawGetContained(BoundingBox area, Collection<T> resultOut) {
        return getContainedObjectsFromNode(rootNode, area, resultOut);
    }

    @Override
    protected int rawGetIntersecting(BoundingBox area, Collection<T> resultOut) {
        return getIntersectingObjectsFromNode(rootNode, area, resultOut);
    }

    private int getContainedObjectsFromNode(Node<T> node, BoundingBox searchArea, Collection<T> resultOut) {
        int numResults = 0;

        if (searchArea.intersects(node.getBounds())) {
            if (node.isLeaf()) {
                // Include contained data objects in result
                for (DataEntry<T> dataEntry : node.getDataEntries()) {
                    if (searchArea.contains(dataEntry.bounds)) {
                        resultOut.add(dataEntry.dataObject);
                        numResults++;
                    }
                }
            }
            else {
                // If non leaf node with overlapping area, check each child node
                for (Node<T> childNode : node.getChildNodes()) {
                    numResults += getContainedObjectsFromNode(childNode, searchArea, resultOut);
                }
            }
        }

        return numResults;
    }

    private int getIntersectingObjectsFromNode(Node<T> node, BoundingBox searchArea, Collection<T> resultOut) {
        int numResults = 0;

        if (searchArea.intersects(node.getBounds())) {
            if (node.isLeaf()) {
                // Include intersecting data objects in result
                for (DataEntry<T> dataEntry : node.getDataEntries()) {
                    if (searchArea.intersects(dataEntry.bounds)) {
                        resultOut.add(dataEntry.dataObject);
                        numResults++;
                    }
                }
            }
            else {
                // If non leaf node with overlapping area, check each child node
                for (Node<T> childNode : node.getChildNodes()) {
                    numResults += getIntersectingObjectsFromNode(childNode, searchArea, resultOut);
                }
            }
        }

        return numResults;
    }


    private double calculateCombinedArea(BoundingBox originalBox, BoundingBox boxToInclude) {
        double x1 = Math.min(originalBox.getMinX(), boxToInclude.getMinX());
        double x2 = Math.max(originalBox.getMaxX(), boxToInclude.getMaxX());
        double y1 = Math.min(originalBox.getMinY(), boxToInclude.getMinY());
        double y2 = Math.max(originalBox.getMaxY(), boxToInclude.getMaxY());
        double xSize = x2 - x1;
        double ySize = y2 - y1;
        return xSize * ySize;
    }

    private Node<T> chooseSubtree(Node<T> parentNode, BoundingBox boundsToAdd) {
        // Find most suitable leaf node to contain the object
        Node<T> node = parentNode;
        while (!node.isLeaf()) {
            // Choose a child node whose boundaries needs least area enlargement to include the new object
            Node<T> bestChildNode = null;
            double bestAreaEnlargementNeeded = Double.POSITIVE_INFINITY;
            for (Node<T> childNode : node.getChildNodes()) {
                double areaEnlargementNeeded =  calculateCombinedArea(childNode.getBounds(), boundsToAdd) - childNode.getBounds().getArea();
                if (bestChildNode == null || areaEnlargementNeeded < bestAreaEnlargementNeeded) {
                    bestChildNode = childNode;
                    bestAreaEnlargementNeeded = areaEnlargementNeeded;
                }
            }
            if (bestChildNode == null) throw new IllegalStateException("A non-leaf node must have at least two child nodes, but no suitable child node was found to add a node to");

            // Descend into the best subnode
            node = bestChildNode;
        }

        return (Node<T>) node;
    }


    /**
     * A node in the RStar spatial index.
     */
    private static final class Node<T> {
        private Node<T> parent = null;
        private final BoundingBox bounds;
        private final List<Node<T>> childNodes;
        private final T content;

        /**
         * Creates a new non-leaf node.
         */
        public Node() {
            this.bounds = new MutableBoundingBox();
            this.content = null;
            this.childNodes = new ArrayList<Node<T>>();
        }

        /**
         * Creates a new leaf node.
         */
        public Node(T content, BoundingBox bounds) {
            if (content == null) throw new IllegalArgumentException("Content should not be null for a leaf node.");
            if (bounds == null) throw new IllegalArgumentException("Bounds should not be null for a leaf node.");

            this.bounds = bounds;
            this.content = content;
            this.childNodes = null;
        }

        public Node<T> getParent() {
            return parent;
        }

        public T getContent() {
            return content;
        }

        public boolean isRoot() {
            return parent == null;
        }

        public boolean isLeaf() {
            return content != null;
        }

        public BoundingBox getBounds() {
            return bounds;
        }

        public List<Node<T>> getChildNodes() {
            return childNodes == null ? Collections.<Node<T>>emptyList() : childNodes;
        }

        public void addChildNode(Node<T> node) {
            if (isLeaf()) throw new IllegalArgumentException("Can not add child nodes to a leaf node");

            // Remove from old parent node
            if (node.getParent() != null) {
                node.getParent().removeChildNode(node);
            }

            // Set parent to this
            node.parent = this;

            // Add to list of child nodes
            childNodes.add(node);

            if (!bounds.contains(node.getBounds())) {
                // Update bounds
                bounds.include(node.getBounds());
            }
        }

        public void removeChildNode(Node<T> node) {
            if (childNodes.remove(node)) {
                node.parent = null;

                // Update bounds
                if (childNodes.isEmpty()) {
                    bounds.clear();
                }
                else {
                    bounds.set(childNodes.get(0));
                    for (int i = 1; i < childNodes.size(); i++) {
                        bounds.include(childNodes.get(i).getBounds());
                    }
                }
            }
        }

        public int getSize() {
            return childNodes == null ? 0 : childNodes.size();
        }

        public void split() {
            if (isLeaf()) throw new IllegalStateException("Can not split a leaf node");

            // Sort nodes by (lower and upper) x and y boundaries  (primary order by upper boundary, secondary by lower).
            List<Node<T>> childNodesByX = childNodes;
            List<Node<T>> childNodesByY = new ArrayList<Node<T>>(childNodes);
            Collections.sort(childNodesByX, NODE_X_COMPARATOR);
            Collections.sort(childNodesByY, NODE_Y_COMPARATOR);

            // Sum together margin values for all distributions
            for (int i = 0; i < childNodesByX.size(); i++) {
                double marginSumA = 0;
                double marginSumB = 0;
                for (int j = 0; j < i; j++) {
                    marginSumA += childNodesByX.get(j).getBounds().getCircumference();
                }
                for (int j = i; j < childNodesByX.size(); j++) {
                    marginSumB += childNodesByX.get(j).getBounds().getCircumference();
                }

                // TODO
            }


            // Determine split axis

            // Determine index to split along

            // Divide node into two, add back to parent.

        }
    }

}
