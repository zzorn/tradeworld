package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;
import org.tradeworld.utils.bbox.MutableBoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * R*-Tree based spatial index.
 */
public class RStarSpatialIndex<T> extends SpatialIndexBase<T> {

    private final int maxFill = 64;
    private final int minFill = (int) (maxFill * 0.4);

    private Node<T> rootNode = new LeafNode<T>(parent);

    @Override
    protected void rawAdd(T object, BoundingBox boundingBox) {
        // Create data entry for the object
        DataEntry<T> entry = new DataEntry<T>(object, boundingBox);

        // Determine where to add
        LeafNode<T> nodeToAddObjectTo = chooseSubtree(rootNode, entry);

        // Add
        nodeToAddObjectTo.addDataEntry(entry);

        // If the chosen subtree got filled to max capacity, split it
        if (nodeToAddObjectTo.getSize() > maxFill) {
            splitNode(nodeToAddObjectTo);
        }

        // TODO
    }

    private void splitNode(LeafNode nodeToSplit) {
        // Sort nodes by (lower and upper) x and y boundaries  (primary order by upper boundary, secondary by lower).

        // Sum together margin values for all distributions


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

    private LeafNode<T> chooseSubtree(Node<T> parentNode, DataEntry<T> entryToAdd) {
        // Find most suitable leaf node to contain the object
        Node<T> node = parentNode;
        while (!node.isLeaf()) {
            // Choose a child node whose boundaries needs least area enlargement to include the new object
            Node<T> bestChildNode = null;
            double bestAreaEnlargementNeeded = Double.POSITIVE_INFINITY;
            for (Node<T> childNode : node.getChildNodes()) {
                double areaEnlargementNeeded =  calculateCombinedArea(childNode.getBounds(), entryToAdd.bounds) - childNode.getBounds().getArea();
                if (bestChildNode == null || areaEnlargementNeeded < bestAreaEnlargementNeeded) {
                    bestChildNode = childNode;
                    bestAreaEnlargementNeeded = areaEnlargementNeeded;
                }
            }
            if (bestChildNode == null) throw new IllegalStateException("A non-leaf node must have at least two child nodes, but no suitable child node was found to add a node to");

            // Descend into the best subnode
            node = bestChildNode;
        }

        return (LeafNode<T>) node;
    }


    /**
     * Base class for nodes in RStar spatial index.
     */
    private static abstract class Node<T> {
        private final Node<T> parent;
        private final MutableBoundingBox bounds = new MutableBoundingBox();

        protected Node(Node<T> parent) {
            this.parent = parent;
        }

        public final Node<T> getParent() {
            return parent;
        }

        public final MutableBoundingBox getBounds() {
            return bounds;
        }

        protected abstract boolean isLeaf();
        protected abstract List<Node<T>> getChildNodes();
        protected abstract List<DataEntry<T>> getDataEntries();
        protected abstract int getSize();
    }

    /**
     * Non-leaf node.  Contains other nodes.
     */
    private static final class InternalNode<T> extends Node<T> {
        private final List<Node<T>> childNodes = new ArrayList<Node<T>>();

        private InternalNode(Node<T> parent) {
            super(parent);
        }

        public List<Node<T>> getChildNodes() {
            return childNodes;
        }

        public void addChildNode(Node<T> node) {
            childNodes.add(node);
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        @Override
        public List<DataEntry<T>> getDataEntries() {
            return Collections.emptyList();
        }

        @Override
        public int getSize() {
            return childNodes.size();
        }
    }

    /**
     * Leaf node.  Contains stored spatial data entries.
     */
    private static final class LeafNode<T> extends Node<T> {
        private final List<DataEntry<T>> dataEntries = new ArrayList<DataEntry<T>>();

        private LeafNode(Node<T> parent) {
            super(parent);
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

        public void addDataEntry(DataEntry<T> dataEntry) {
            dataEntries.add(dataEntry);
        }

        @Override
        public List<Node<T>> getChildNodes() {
            return Collections.emptyList();
        }

        public List<DataEntry<T>> getDataEntries() {
            return dataEntries;
        }

        @Override
        public int getSize() {
            return dataEntries.size();
        }
    }

    private static final class DataEntry<T> {
        public final T dataObject;
        public final BoundingBox bounds;

        private DataEntry(T dataObject, BoundingBox bounds) {
            this.dataObject = dataObject;
            this.bounds = bounds;
        }
    }

}
