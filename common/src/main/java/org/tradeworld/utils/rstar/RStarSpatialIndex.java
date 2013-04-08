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

    private Node<T> rootNode = new LeafNode<T>();

    @Override
    protected void rawAdd(T object, BoundingBox boundingBox) {
        // Create data entry for the object
        DataEntry<T> entry = new DataEntry<T>(object, boundingBox);

        // Determine where to add
        LeafNode<T> nodeToAddObjectTo = chooseSubtree(rootNode, entry);

        // Add
        nodeToAddObjectTo.addDataEntry(entry);

        // If the chosen subtree got filled to max capacity, split it

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

        if (searchArea.intersects(node.getArea())) {
            if (node.isLeaf()) {
                // Include contained data objects in result
                for (DataEntry<T> dataEntry : node.getDataEntries()) {
                    if (searchArea.contains(dataEntry.area)) {
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

        if (searchArea.intersects(node.getArea())) {
            if (node.isLeaf()) {
                // Include intersecting data objects in result
                for (DataEntry<T> dataEntry : node.getDataEntries()) {
                    if (searchArea.intersects(dataEntry.area)) {
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


    private LeafNode<T> chooseSubtree(Node<T> parentNode, DataEntry<T> entryToAdd) {
        // Descend to leaf

        Node<T> node = parentNode;

        // At every level

        // Find most suitable subtree to contain the object
        Node<T> bestSubNode = null;
        while (!node.isLeaf()) {
            for (Node<T> childNode : node.getChildNodes()) {
                // If is better child node than best node
                bestSubNode = childNode;
            }

            // Descend into the best subnode
            node = bestSubNode;
        }

        return (LeafNode<T>) node;



        // Set N to be root node

        // If N is leaf, return N

        // Else,

        // Choose an entry in N whose rectangle leeds least area enlargement to include the new object
        // Resolve ties by choosing the entry with the rectangle of the smallest area

        // Set N to be the chosen entry and repeat

    }


    /**
     * Node in RStar spatial index.
     */
    private interface Node<T> {
        MutableBoundingBox getArea();

        boolean isLeaf();

        List<Node<T>> getChildNodes();
        List<DataEntry<T>> getDataEntries();

    }

    private static final class InternalNode<T> implements Node<T> {
        private final MutableBoundingBox area = new MutableBoundingBox();
        private final List<Node<T>> childNodes = new ArrayList<Node<T>>();

        public MutableBoundingBox getArea() {
            return area;
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
    }

    private static final class LeafNode<T> implements Node<T> {
        private final MutableBoundingBox area = new MutableBoundingBox();
        private final List<DataEntry<T>> dataEntries = new ArrayList<DataEntry<T>>();

        public MutableBoundingBox getArea() {
            return area;
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
    }

    private static final class DataEntry<T> {
        public final T dataObject;
        public final BoundingBox area;

        private DataEntry(T dataObject, BoundingBox area) {
            this.dataObject = dataObject;
            this.area = area;
        }
    }

}
