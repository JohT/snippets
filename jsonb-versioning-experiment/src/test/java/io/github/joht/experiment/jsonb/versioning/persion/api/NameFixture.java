package io.github.joht.experiment.jsonb.versioning.persion.api;

import io.github.joht.experiment.jsonb.versioning.persion.api.Name.V1;

/**
 * Object-Mother for Test-{@link Name}s.<br>
 * Provides preset {@link Name}-Objects to be used as test fixtures.
 * 
 * @author JohT
 */
public enum NameFixture {

    JOHN_DOE {
        @Override
        public String getExpectedJsonRepresentation(int version) {
            if (version == 1) {
                return "{\"forename\":\"John\",\"surename\":\"Doe\"}";
            }
            return "{\"forename\":\"John\",\"surename\":\"Doe\",\"title\":\"Dr\"}";
        }

        @Override
        public Name buildCurrent() {
            Name name = new Name();
            name.forename = "John";
            name.surename = "Doe";
            name.title = "Dr";
            return name;
        }

        @Override
        public V1 buildV1() {
            Name.V1 name = new Name.V1();
            name.forename = "John";
            name.surename = "Doe";
            return name;
        }
    },

    ;

    public abstract Name buildCurrent();

    public abstract Name.V1 buildV1();

    public abstract String getExpectedJsonRepresentation(int version);

    public String getExpectedJsonRepresentationForTheCurrentVersion() {
        return getExpectedJsonRepresentation(0);
    }
}
