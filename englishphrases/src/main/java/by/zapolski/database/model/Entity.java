package by.zapolski.database.model;

import lombok.ToString;

import java.io.Serializable;

@ToString
public abstract class Entity implements Serializable, Cloneable {
    private int id;

    public Entity() {
    }

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
