package io.github.joht.sample.basic;

import java.util.Objects;

public class ObjectWithEqualsHashcode {

    private final long id;
    private final String name;

    public ObjectWithEqualsHashcode(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        ObjectWithEqualsHashcode castOther = (ObjectWithEqualsHashcode) other;
        return Objects.equals(id, castOther.id) && Objects.equals(name, castOther.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "ObjectWithEqualsHashcode [id=" + id + ", name=" + name + "]";
    }
}
