package io.github.joht.experiment.jsonb.versioning.persion.jsonb;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;

import io.github.joht.experiment.jsonb.versioning.persion.VersioningSupport;
import io.github.joht.experiment.jsonb.versioning.persion.api.Person;

public class PersonAdapter implements JsonbAdapter<Person, JsonObject> {

    private final VersioningSupport<Person> versioning;

    public PersonAdapter(JsonbConfig config) {
        Jsonb jsonb = JsonbBuilder.create(config);
        this.versioning = new VersioningSupport<>(Person.class, jsonb::toJson, jsonb::fromJson);
    }

    @Override
    public JsonObject adaptToJson(Person obj) {
        return versioning.reserialize(obj, JsonObject.class);
    }

    @Override
    public Person adaptFromJson(JsonObject obj) {
        return versioning.adaptFromJson(obj.toString());
    }
}