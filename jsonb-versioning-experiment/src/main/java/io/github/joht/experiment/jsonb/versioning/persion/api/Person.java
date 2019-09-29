package io.github.joht.experiment.jsonb.versioning.persion.api;

/**
 * Represents a Person.
 * <p>
 * To keep it simple, all properties are <code>public</code>. <br>
 * TODO Expand to immutable object.
 * 
 * @author JohT
 */
public class Person {
    public Name name;

    public static final int getVersion(String json) {
        // Note: Just an example, could be done more controlled using a version field.
        if (json.contains("\"title\"")) {
            return 0; // = current version
        }
        if (json.contains("\"name\":{")) {
            return 3;
        }
        if (json.contains("\"forename\"")) {
            return 2;
        }
        return 1;
    }

    public static final Person update(Person.V1 previous) {
        Person person = new Person();
        person.name = Name.upcast(previous.name);
        return person;
    }

    public static final Person update(Person.V2 previous) {
        Name name = new Name();
        name.forename = previous.forename;
        name.surename = previous.surename;
        Person person = new Person();
        person.name = name;
        return person;
    }

    public static final Person update(Person.V3 previous) {
        Person person = new Person();
        person.name = Name.upcast(previous.name);
        return person;
    }

    public static class V1 {
        public String name;
    }

    public static class V2 {
        public String forename;
        public String surename;
    }

    public static class V3 {
        public Name.V1 name;
    }
}