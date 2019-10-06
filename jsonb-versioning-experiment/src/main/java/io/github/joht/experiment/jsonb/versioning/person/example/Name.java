package io.github.joht.experiment.jsonb.versioning.person.example;

/**
 * Represents a Name.
 * <p>
 * To keep it simple, all properties are <code>public</code>. <br>
 * TODO Expand to immutable object.
 * 
 * @author JohT
 */
public class Name {
    public String forename;
    public String surename;
    public String title;

    public static final int versionOf(String json) {
        // Note: Just a simple example, could be done more controlled using a version field.
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

    public static class V1 {
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