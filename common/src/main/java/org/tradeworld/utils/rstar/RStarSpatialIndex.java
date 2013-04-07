package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;

import java.util.Collection;

/**
 * R*-Tree based spatial index.
 */
public class RStarSpatialIndex<T> extends SpatialIndexBase<T> {

    @Override
    protected void rawAdd(T object, BoundingBox boundingBox) {
        // Begin at root

        // Descend to leaf

        // At every level

        // Find most suitable subtree to contain the object

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
        // Start at root

        // Find all child nodes overlapping the search area

        // Recurse

        // Add all leafs contained in search area to result

        // TODO
        return 0;
    }

    @Override
    protected int rawGetIntersecting(BoundingBox area, Collection<T> resultOut) {
        // Start at root

        // Find all child nodes overlapping the search area

        // Recurse

        // Add all leafs intersecting the search area to result

        // TODO
        return 0;
    }


    private void chooseSubtree() {
        // Set N to be root node

        // If N is leaf, return N

        // Else,

        // Choose an entry in N whose rectangle leeds least area enlargement to include the new object
        // Resolve ties by choosing the entry with the rectangle of the smallest area

        // Set N to be the chosen entry and repeat
    }




}
