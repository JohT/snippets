package io.github.joht.experiment.jsonb.versioning.person.example.jsonb.generic;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.experiment.jsonb.versioning.person.example.Person;
import io.github.joht.experiment.jsonb.versioning.person.example.PersonFixture;

class GenericVersionPersonDeserializerTest {

    private final JsonbConfig jsonbConfig = new JsonbConfig();
    private final Jsonb jsonb = JsonbBuilder.create(jsonbConfig.withDeserializers(GenericVersionDeserializer.forConfig(jsonbConfig)));
    private final PersonFixture personFixture = PersonFixture.JOHN_DOE;

    @Test
    void version1Deserializable() {
        Person.V1 person = personFixture.buildV1();
        Person deserialized = jsonb.fromJson(jsonb.toJson(person), Person.class);
        assertEquals(person.name, deserialized.name.forename + " " + deserialized.name.surename);
    }

    @Test
    void version2Deserializable() {
        Person.V2 person = personFixture.buildV2();
        Person deserialized = jsonb.fromJson(jsonb.toJson(person), Person.class);
        assertEquals(person.forename, deserialized.name.forename);
        assertEquals(person.surename, deserialized.name.surename);
    }

    @Test
    void version3Deserializable() {
        Person.V3 person = personFixture.buildV3();
        Person deserialized = jsonb.fromJson(jsonb.toJson(person), Person.class);
        assertEquals(person.name.forename, deserialized.name.forename);
        assertEquals(person.name.surename, deserialized.name.surename);
    }

    @Test
    void currentVersionDeserializable() {
        Person person = personFixture.buildCurrent();
        Person deserialized = jsonb.fromJson(jsonb.toJson(person), Person.class);
        assertEquals(person.name.forename, deserialized.name.forename);
        assertEquals(person.name.surename, deserialized.name.surename);
        assertEquals(person.name.title, deserialized.name.title);
    }

}
