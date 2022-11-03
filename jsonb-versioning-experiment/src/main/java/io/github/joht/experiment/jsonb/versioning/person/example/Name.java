package io.github.joht.experiment.jsonb.versioning.person.example;

import java.io.Serializable;

/**
 * Represents a Name.
 * <p>
 * To keep it simple, all properties are <code>public</code>. <br>
 * <p>
 * Since a Deserializer can't be registered for all Objects (anymore since yasson 1.0.8),
 * there needs to be a common super type, here in this example Serializable.
 * 
 * TODO Expand to immutable object.
 * 
 * @author JohT
 */
public class Name implements Serializable {

    public String forename;
    public String surename;
    public String title;

    public static final int versionOf(String json) {
        // Note: Just a simple example, 
        // could be done more controlled e.g. using a version field.
        if (json.contains("\"title\"")) {
            return 0; // = current version
        }
        return 1;
    }

    public static final Name from(String previous) {
        Name name = new Name();
        String[] words = (previous != null) ? previous.split("\\s") : new String[0];
        name.forename = (words.length > 0) ? words[0] : "";
        name.surename = (words.length > 1) ? words[1] : "";
        name.title = "";
        return name;
    }

    public static class V1 implements Serializable {

        public String forename;
        public String surename;

        public Name toCurrentVersion() {
            Name name = new Name();
            name.forename = this.forename;
            name.surename = this.surename;
            name.title = "";
            return name;
        }
    }

}