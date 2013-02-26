package org.tradeworld.systems.geography;

import org.tradeworld.entity.BaseComponent;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Something that is located at some position and perceives geographical formations.
 */
public class GeographyPerceptor extends BaseComponent {

    private Queue<GeographyPerception> perceptions = new ArrayDeque<GeographyPerception>();

    public void queuePerception(GeographyPerception geographyPerception) {
        perceptions.add(geographyPerception);
    }
}
