package io.github.joht.sample;

import java.util.Objects;

public class SampleEntity {

    private final long id;
    private String name = "";

    public SampleEntity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        SampleEntity castOther = (SampleEntity) other;
        return Objects.equals(id, castOther.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "SampleEntity [id=" + id + ", name=" + name + "]";
    }
}