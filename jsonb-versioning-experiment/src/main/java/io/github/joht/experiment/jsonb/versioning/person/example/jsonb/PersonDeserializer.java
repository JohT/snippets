package io.github.joht.experiment.jsonb.versioning.person.example.jsonb;

import java.lang.reflect.Type;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import io.github.joht.experiment.jsonb.versioning.VersioningSupport;
import io.github.joht.experiment.jsonb.versioning.person.example.Person;

/**
 * Deserializes {@link Person}-Objects supporting all previous version.
 * <p>
 * TODO Generic versioning maybe using an interface or a wrapper.
 * 
 * @author JohT
 */
public class PersonDeserializer implements JsonbDeserializer<Person> {

    private final VersioningSupport<Person> versioning;

    public PersonDeserializer(JsonbConfig config) {
        Jsonb jsonb = JsonbBuilder.create(config);
        this.versioning = new VersioningSupport<>(Person.class, jsonb::toJson, jsonb::fromJson);
    }

    @Override
    public Person deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        return versioning.adaptFromJson(parser.getObject().toString());
    }
}