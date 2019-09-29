package io.github.joht.experiment.jsonb.versioning.persion.api;

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

    public static final Name upcast(String previous) {
        Name name = new Name();
        String[] words = (previous != null) ? previous.split("\\s") : new String[0];
        name.forename = (words.length > 0) ? words[0] : "";
        name.surename = (words.length > 1) ? words[1] : "";
        name.title = "";
        return name;
    }

    public static final Name upcast(Name.V1 previous) {
        Name name = new Name();
        name.forename = previous.forename;
        name.surename = previous.surename;
        name.title = "";
        return name;
    }

    public static class V1 {
        public String forename;
        public String surename;
    }
}