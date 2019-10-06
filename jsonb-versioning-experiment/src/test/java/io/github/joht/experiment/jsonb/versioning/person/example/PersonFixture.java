package io.github.joht.experiment.jsonb.versioning.person.example;

import io.github.joht.experiment.jsonb.versioning.person.example.Person.V1;
import io.github.joht.experiment.jsonb.versioning.person.example.Person.V2;
import io.github.joht.experiment.jsonb.versioning.person.example.Person.V3;

/**
 * Object-Mother for Test-{@link Person}s. <br>
 * Provides preset {@link Person}-Objects to be used as test fixtures.
 * 
 * @author JohT
 */
public enum PersonFixture {

    JOHN_DOE {
        @Override
        public String getExpectedJsonRepresentation(int version) {
            if (version == 1) {
                return "{\"name\":\"John Doe\"}";
            } else if (version == 2) {
                return "{\"forename\":\"John\",\"surename\":\"Doe\"}";
            } else if (version == 3) {
                return "{\"name\":" + NameFixture.JOHN_DOE.getExpectedJsonRepresentation(1) + "}";
            }
            return "{\"name\":" + NameFixture.JOHN_DOE.getExpectedJsonRepresentation(0) + "}";
        }

        @Override
        public V1 buildV1() {
            Person.V1 person = new Person.V1();
            person.name = "John Doe";
            return person;
        }

        @Override
        public V2 buildV2() {
            Person.V2 person = new Person.V2();
            person.forename = "John";
            person.surename = "Doe";
            return person;
        }

        @Override
        public V3 buildV3() {
            Person.V3 person = new Person.V3();
            person.name = NameFixture.JOHN_DOE.buildV1();
            return person;
        }

        @Override
        public Person buildCurrent() {
            Person person = new Person();
            person.name = NameFixture.JOHN_DOE.buildCurrent();
            return person;
        }

    },

    ;

    public abstract Person buildCurrent();

    public abstract Person.V1 buildV1();

    public abstract Person.V2 buildV2();

    public abstract Person.V3 buildV3();

    public abstract String getExpectedJsonRepresentation(int version);

    public String getExpectedJsonRepresentationForTheCurrentVersion() {
        return getExpectedJsonRepresentation(0);
    }

}
