package org.tradeworld.components;

import org.tradeworld.entity.BaseComponent;

/**
 * Something that can be eaten, and has some effect on a body.
 */
public class Edible extends BaseComponent {

    public float energyJoule = 10;
    public float poisonDamage = 0;
    public float heal = 0;

    public Edible(float energyJoule, float poisonDamage, float heal) {
        this.energyJoule = energyJoule;
        this.poisonDamage = poisonDamage;
        this.heal = heal;
    }

    @Override
    public String toString() {
        return "Edible{" +
                "energyJoule=" + energyJoule +
                ", poisonDamage=" + poisonDamage +
                ", heal=" + heal +
                '}';
    }
}
