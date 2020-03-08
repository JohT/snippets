package io.github.joht.sample.basic;

public class ObjectWithoutEqualsHashcode {

    private final long id;
    private final String name;

    public ObjectWithoutEqualsHashcode(long id, String name) {
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
    public String toString() {
        return "ObjectWithoutEqualsHashcode [id=" + id + ", name=" + name + "]";
    }
}
