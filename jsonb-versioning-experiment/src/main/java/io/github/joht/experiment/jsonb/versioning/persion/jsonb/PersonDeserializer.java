package io.github.joht.experiment.jsonb.versioning.persion.jsonb;

import java.lang.reflect.Type;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import io.github.joht.experiment.jsonb.versioning.persion.api.Person;

//FIXME Ends in an endless loop. To be implemented right....
public class PersonDeserializer implements JsonbDeserializer<Person> {

    @Override
    public Person deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        return ctx.deserialize(rtType, parser);
    }
}