package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;
import org.tradeworld.utils.bbox.MutableBoundingBox;

import java.util.*;

/**
 * R*-Tree based spatial index.
 */
public class RStarSpatialIndex<T> extends SpatialIndexBase<T> {

    // TODO: Make maxFill and other parameters parametrizable
    private static final int maxFill = 64;
    private static final int minFill = (int) (maxFill * 0.4);
    private static final double RE_INSERTION_FRACTION = 0.3;

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

        addNode(entry, new BitSet());
    }

    private void addNode(Node<T> entry, BitSet overflowsAtDepths) {
        // Determine where to add
        Node<T> nodeToAddObjectTo = chooseSubtree(rootNode, entry.getBounds());

        // Add
        nodeToAddObjectTo.addChildNode(entry);

        // Check if the chosen subtree got filled to max capacity
        if (nodeToAddObjectTo.getSize() > maxFill) {

            // Handle overflow, first time at each depth level by re-inserting some of the nodes, subsequent times by splitting the node.
            int depth = nodeToAddObjectTo.getDepth();
            if (depth > 0 && !overflowsAtDepths.get(depth)) {

                // Mark overflow handled at this depth
                overflowsAtDepths.set(depth);

                // Remove some nodes from the overflowing node, and add them back to the root.
                reInsertSomeNodes(nodeToAddObjectTo, overflowsAtDepths);
            }
            else {
                // If we are splitting the root, create a new root and add the old root to it first.
                if (nodeToAddObjectTo == rootNode) {
                    rootNode = new Node<T>();
                    rootNode.addChildNode(nodeToAddObjectTo);
                }

                // Split node into two
                nodeToAddObjectTo.split();
            }
        }
    }

    private void reInsertSomeNodes(Node<T> overFilledNode, BitSet overflowsAtDepths) {
        if (overFilledNode.isLeaf()) throw new IllegalStateException("reInsert not possible for leaf nodes");

        // Sort nodes by distance from child node centers to the center of this node
        final double centerX = overFilledNode.getBounds().getCenterX();
        final double centerY = overFilledNode.getBounds().getCenterY();
        Collections.sort(overFilledNode.childNodes, new Comparator<Node<T>>() {
            @Override
            public int compare(Node<T> o1, Node<T> o2) {
                double d1 = o1.getBounds().getSquaredCenterDistanceTo(centerX, centerY);
                double d2 = o2.getBounds().getSquaredCenterDistanceTo(centerX, centerY);

                if (d1 < d2) return -1;
                else if (d1 > d2) return 1;
                else return 0;
            }
        });

        // Remove some percentage of furthest away ones
        int nodesToRemoveCount = (int) (overFilledNode.getSize() * RE_INSERTION_FRACTION);
        List<Node<T>> removedNodes = new ArrayList<Node<T>>(nodesToRemoveCount);
        for (int i = overFilledNode.getSize() - 1; i >= overFilledNode.getSize() - nodesToRemoveCount; i--) {
            removedNodes.add(overFilledNode.childNodes.get(i));
            overFilledNode.childNodes.remove(i);
        }

        // Recalculate node bounds
        overFilledNode.recalculateBounds(true);

        // Re-insert removed nodes
        for (int i = removedNodes.size() - 1; i >= 0; i--) {
            Node<T> removedNode = removedNodes.get(i);
            addNode(removedNode, overflowsAtDepths);
        }
    }


    @Override
    protected void rawRemove(T object) {
        final BoundingBox boundingBox = getBoundingBox(object);
        if (boundingBox != null) {
            // Find node for the object
            Node<T> node = rootNode.getNodeFor(object, boundingBox);

            if (node != null) {
                // Remove node
                final Node<T> parent = node.getParent();
                parent.removeChildNode(node);

                // If parent has less than min capacity, merge
                if (!parent.isRoot() && parent.getSize() < minFill) {
                    // Remove parent node
                    parent.getParent().removeChildNode(parent);

                    // Add all its children to the tree
                    BitSet overflowsAtDepths = new BitSet();
                    for (Node<T> childNode : parent.getChildNodes()) {
                        //overflowsAtDepths.clear();
                        addNode(childNode, overflowsAtDepths);
                    }
                }
            }
        }
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

        if (node.isLeaf() && searchArea.contains(node.getBounds())) {
            // Include data object in result
            resultOut.add(node.getContent());
            numResults++;
        }
        else if (searchArea.intersects(node.getBounds())) {
            // If non-leaf node with overlapping area, check each child node
            for (Node<T> childNode : node.getChildNodes()) {
                numResults += getContainedObjectsFromNode(childNode, searchArea, resultOut);
            }
        }

        return numResults;
    }

    private int getIntersectingObjectsFromNode(Node<T> node, BoundingBox searchArea, Collection<T> resultOut) {
        int numResults = 0;

        if (searchArea.intersects(node.getBounds())) {
            if (node.isLeaf()) {
                // Include intersecting data object in result
                resultOut.add(node.getContent());
                numResults++;
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

        return node;
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

        public Node<T> getRoot() {
            if (parent == null) return this;
            else return parent.getRoot();
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

            // Add node
            rawAddChildNode(node);

            // Update bounds
            includeInBounds(node.getBounds());
        }

        public int getDepth() {
            if (parent == null) return 0;
            else return parent.getDepth() + 1;
        }

        private void includeInBounds(final BoundingBox boundsToInclude) {
            // Update bounds
            ((MutableBoundingBox) bounds).include(boundsToInclude);

            // Notify parent as well
            if (parent != null) parent.includeInBounds(boundsToInclude);
        }

        private void rawAddChildNode(Node<T> node) {
            // Set parent to this
            node.parent = this;

            // Add to list of child nodes
            childNodes.add(node);
        }

        public void removeChildNode(Node<T> node) {
            if (isLeaf()) throw new IllegalStateException("Can not remove a child node from a leaf node");

            if (childNodes.remove(node)) {
                node.parent = null;

                // Update bounds
                recalculateBounds(true);
            }
        }

        public int getSize() {
            return childNodes == null ? 0 : childNodes.size();
        }

        public void split() {
            if (isLeaf()) throw new IllegalStateException("Can not split a leaf node");
            if (isRoot()) throw new IllegalStateException("Can not split the root node");

            // Sort nodes by (lower and upper) x and y boundaries  (primary order by upper boundary, secondary by lower).
            List<Node<T>> childNodesByX = childNodes;
            List<Node<T>> childNodesByY = new ArrayList<Node<T>>(childNodes);
            Collections.sort(childNodesByX, NODE_X_COMPARATOR);
            Collections.sort(childNodesByY, NODE_Y_COMPARATOR);

            // Determine split axis - use the axis with the smallest circumference sum of it's distributions
            double circumferenceSumX = calculateCircumferenceSum(childNodesByX);
            double circumferenceSumY = calculateCircumferenceSum(childNodesByY);
            boolean splitAlongX = circumferenceSumX <= circumferenceSumY;
            List<Node<T>> sortedChildren;
            if (splitAlongX) sortedChildren = childNodesByX;
            else sortedChildren = childNodesByY;

            // Determine index to split at
            final int splitIndex = chooseSplitIndex(sortedChildren);

            // Split off new node
            Node<T> splitNode = new Node<T>();
            for (int i = splitIndex; i < sortedChildren.size(); i++) {
                final Node<T> childNode = sortedChildren.get(i);

                // Add to new node
                splitNode.rawAddChildNode(childNode);

                // Remove from this node
                childNodes.remove(childNode);
            }

            // Recalculate bounds
            recalculateBounds(false);
            splitNode.recalculateBounds(false);

            // Add split node to parent
            parent.addChildNode(splitNode);
        }

        private double calculateCircumferenceSum(List<Node<T>> nodes) {
            double sum = 0;
            MutableBoundingBox tempBounds = new MutableBoundingBox();

            // Iterate all distribution of the cells where they are split into two groups
            for (int splitIndex = minFill; splitIndex < nodes.size() - minFill; splitIndex++) {
                sum += boundingBoxForNodeSubset(nodes, 0, splitIndex, tempBounds).getCircumference();
                sum += boundingBoxForNodeSubset(nodes, splitIndex, nodes.size(), tempBounds).getCircumference();
            }

            return sum;
        }

        private int chooseSplitIndex(List<Node<T>> nodes) {
            int bestIndex = 0;
            double minimumOverlap = Double.POSITIVE_INFINITY;
            MutableBoundingBox tempBoundsA = new MutableBoundingBox();
            MutableBoundingBox tempBoundsB = new MutableBoundingBox();

            // Iterate all distributions, find the one with smallest overlap between the distributions
            for (int splitIndex = minFill; splitIndex < nodes.size() - minFill; splitIndex++) {
                // Calculate bounds for the two subsets
                boundingBoxForNodeSubset(nodes, 0, splitIndex, tempBoundsA);
                boundingBoxForNodeSubset(nodes, splitIndex, nodes.size(), tempBoundsB);

                // Get area of intersection
                tempBoundsA.setToIntersection(tempBoundsB);
                final double overlapArea = tempBoundsA.getArea();

                // Find smallest overlap
                if (overlapArea < minimumOverlap) {
                    minimumOverlap = overlapArea;
                    bestIndex = splitIndex;
                }
            }

            return bestIndex;
        }

        private MutableBoundingBox boundingBoxForNodeSubset(List<Node<T>> nodes, final int start, int end, MutableBoundingBox boundsOut) {
            if (boundsOut == null) boundsOut = new MutableBoundingBox();

            boundsOut.clear();
            if (start < nodes.size()) {
                boundsOut.set(nodes.get(start).getBounds());
                for (int i = start + 1; i < end; i++) {
                    boundsOut.include(nodes.get(i).getBounds());
                }
            }
            return boundsOut;
        }


        private void recalculateBounds(boolean propagateToParent) {
            if (isLeaf()) throw new IllegalStateException("Can not clear bounds for leaf node");

            final MutableBoundingBox mutableBounds = (MutableBoundingBox) bounds;

            mutableBounds.clear();

            if (childNodes != null) {
                for (Node<T> childNode : childNodes) {
                    mutableBounds.include(childNode.getBounds());
                }
            }

            if (propagateToParent && parent != null) {
                // Recalculate bounds for parent as well.
                parent.recalculateBounds(true);
            }
        }


        public Node<T> getNodeFor(T object, BoundingBox boundingBox) {
            if (bounds.intersects(boundingBox)) {
                if (isLeaf()) {
                    if (content == object) {
                        return this;
                    }
                }
                else {
                    for (Node<T> childNode : getChildNodes()) {
                        final Node<T> node = childNode.getNodeFor(object, boundingBox);
                        if (node != null) return node;
                    }
                }
            }

            return null;
        }

    }

}
