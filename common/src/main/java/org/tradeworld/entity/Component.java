package org.tradeworld.entity;

/**
 * Part of an Entity.  Holds data for some specific aspect of the entity.
 */
public interface Component {

    /**
     * @return id of the type of component this component is.
     */
    int getComponentTypeId();

    /**
     * Called when the component instance is added to an entity.
     * @param entity the entity it is added to.
     */
    void setEntity(Entity entity);

    /**
     * Called when a component is removed from an entity.
     * May do any needed memory de-allocation.
     */
    void onRemoved();

    Class<? extends Component> getBaseType();
}
