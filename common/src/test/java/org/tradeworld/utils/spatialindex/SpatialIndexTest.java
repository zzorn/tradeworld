package org.tradeworld.utils.spatialindex;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tradeworld.utils.Stopwatch;
import org.tradeworld.utils.bbox.ImmutableBoundingBox;
import org.tradeworld.utils.rstar.BruteForceSpatialIndex;
import org.tradeworld.utils.rstar.RStarSpatialIndex;
import org.tradeworld.utils.rstar.SpatialIndex;

import java.util.*;

/**
 * Runs some tests with rstar and brute force spatial indexes.
 */
public class SpatialIndexTest {

    private List<SpatialIndex<TestNode>> spatialIndexes;
    private Random random;

    @Before
    public void setUp() throws Exception {
        spatialIndexes = new ArrayList<SpatialIndex<TestNode>>();
        spatialIndexes.add(new BruteForceSpatialIndex<TestNode>());
        spatialIndexes.add(new RStarSpatialIndex<TestNode>());
        random = new Random(923453);
    }

    @Test
    public void testAddAndRetrieve() throws Exception {
        Stopwatch stopwatch = new Stopwatch();

        // Add
        for (SpatialIndex<TestNode> spatialIndex : spatialIndexes) {
            stopwatch.start(1000);
            for (int i = 0; i < 100000; i++) {
                spatialIndex.add(new TestNode("TestObject_" + i, createRandomBoundingBox()));
                stopwatch.lap();
            }
            stopwatch.printResult("Adding an element to " + spatialIndex.getClass().getSimpleName());
        }

        // Retrieve contained
        ArrayList<TestNode> resultOut = new ArrayList<TestNode>();
        for (SpatialIndex<TestNode> spatialIndex : spatialIndexes) {
            stopwatch.start(100);
            for (int i = 0; i < 10000; i++) {
                resultOut.clear();
                spatialIndex.getContained(createRandomBoundingBox(10, 100), resultOut);
                stopwatch.lap();
            }
            stopwatch.printResult("Retrieving contained elements from " + spatialIndex.getClass().getSimpleName());
        }

        // Retrieve intersecting
        for (SpatialIndex<TestNode> spatialIndex : spatialIndexes) {
            stopwatch.start(100);
            for (int i = 0; i < 10000; i++) {
                resultOut.clear();
                spatialIndex.getIntersecting(createRandomBoundingBox(10, 100), resultOut);
                stopwatch.lap();
            }
            stopwatch.printResult("Retrieving intersecting elements from " + spatialIndex.getClass().getSimpleName());
        }

        // Verify results same for contains search
        Set<TestNode> referenceResult = new HashSet<TestNode>();
        Set<TestNode> currentResult = new HashSet<TestNode>();
        int missmatchesFound = 0;
        for (int i = 0; i < 1000; i++) {
            ImmutableBoundingBox bounds = createRandomBoundingBox();
            boolean first = true;
            for (SpatialIndex<TestNode> spatialIndex : spatialIndexes) {
                currentResult.clear();
                spatialIndex.getContained(bounds, currentResult);

                if (first) {
                    first = false;
                    referenceResult.clear();
                    referenceResult.addAll(currentResult);
                }
                else {
                    if (setsEqual(referenceResult, currentResult) != null) missmatchesFound++;
                    //assertSetsEqual("The result should be the same for all spatial index implementations", referenceResult, currentResult);
                }

            }
        }

        // Verify results same for intersection search
        for (int i = 0; i < 1000; i++) {
            ImmutableBoundingBox bounds = createRandomBoundingBox();
            boolean first = true;
            for (SpatialIndex<TestNode> spatialIndex : spatialIndexes) {
                currentResult.clear();
                spatialIndex.getIntersecting(bounds, currentResult);

                if (first) {
                    first = false;
                    referenceResult.clear();
                    referenceResult.addAll(currentResult);
                }
                else {
                    if (setsEqual(referenceResult, currentResult) != null) missmatchesFound++;
                    //assertSetsEqual("The result should be the same for all spatial index implementations", referenceResult, currentResult);
                }

            }
        }
        Assert.assertEquals("Some results missmatch.", 0, missmatchesFound);
    }

    private void assertSetsEqual(String message, Set<String> a, Set<String> b) {
        String missmatch = setsEqual(a, b);
        Assert.assertNull(message + " element " + missmatch + " contained only in one of the sets", missmatch);
    }

    private <T> T setsEqual(Set<T> a, Set<T> b) {
        for (T element : b) {
            if (!a.contains(element)) return element;
        }
        for (T element : a) {
            if (!b.contains(element)) return element;
        }
        return null;
    }

    private ImmutableBoundingBox createRandomBoundingBox() {
        return createRandomBoundingBox(1, 1000);
    }

    private ImmutableBoundingBox createRandomBoundingBox(double size, double distribution) {
        if (random.nextBoolean()) {
            // Gaussian distributed
            double x = random.nextGaussian() * distribution;
            double y = random.nextGaussian() * distribution;
            return new ImmutableBoundingBox(x,
                                            y,
                                            x + random.nextGaussian()*size,
                                            y + random.nextGaussian()*size);
        }
        else {
            // Integer aligned
            int x = random.nextInt((int) distribution);
            int y = random.nextInt((int) distribution);
            return new ImmutableBoundingBox(x,
                                            y,
                                            x + random.nextInt((int) size),
                                            y + random.nextInt((int) size));
        }
    }

    public static void main(String[] args) {

    }



}
