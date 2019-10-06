package io.github.joht.experiment.jsonb.versioning.persion.api;

/**
 * Represents a Person.
 * <p>
 * To keep it short and simple, all properties are <code>public</code>. <br>
 * 
 * @author JohT
 */
public class Person {
    public Name name;

    public static final int getVersion(String json) {
        // Note: Just a simple example, could be done more controlled using a version field.
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

    public static class V1 {
        public String name;

        public Person toCurrentVersion() {
            Person person = new Person();
            person.name = Name.from(this.name);
            return person;
        }
    }

    public static class V2 {
        public String forename;
        public String surename;

        public Person toCurrentVersion() {
            Name name = new Name();
            name.forename = this.forename;
            name.surename = this.surename;
            Person person = new Person();
            person.name = name;
            return person;
        }
    }

    public static class V3 {
        public Name.V1 name;

        public Person toCurrentVersion() {
            Person person = new Person();
            person.name = this.name.toCurrentVersion();
            return person;
        }
    }
}