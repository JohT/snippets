package io.github.joht.experiment.jsonb.versioning.person.example;

import java.io.Serializable;

/**
 * Represents a Person.
 * <p>
 * To keep it short and simple, all properties are <code>public</code>. <br>
 * <p>
 * Since a Deserializer can't be registered for all Objects (anymore since yasson 1.0.8),
 * there needs to be a common super type, here in this example Serializable.
 * 
 * @author JohT
 */
public class Person implements Serializable {

    public Name name;

    public static final int versionOf(String json) {
        // Note: Just a simple example, 
        // could be done more controlled e.g.using a version field.
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

    public static class V1 implements Serializable {

        public String name;

        public Person toCurrentVersion() {
            Person person = new Person();
            person.name = Name.from(this.name);
            return person;
        }
    }

    public static class V2 implements Serializable {

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

    public static class V3 implements Serializable {

        public Name.V1 name;

        public Person toCurrentVersion() {
            Person person = new Person();
            person.name = this.name.toCurrentVersion();
            return person;
        }
    }
}