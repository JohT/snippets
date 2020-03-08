package io.github.joht.sample;

import java.util.Objects;

public class SampleValueObject {

    private final long id;
    private final String name;

    public SampleValueObject(long id, String name) {
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
        SampleValueObject castOther = (SampleValueObject) other;
        return Objects.equals(id, castOther.id) && Objects.equals(name, castOther.name);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "SampleValueObject [id=" + id + ", name=" + name + "]";
    }
}