package org.tradeworld.components;

import org.tradeworld.entity.BaseComponent;

/**
 * Something with a name.
 */
public class Named extends BaseComponent {

    public String name;

    public Named() {
    }

    public Named(String name) {
        this.name = name;
    }
}
