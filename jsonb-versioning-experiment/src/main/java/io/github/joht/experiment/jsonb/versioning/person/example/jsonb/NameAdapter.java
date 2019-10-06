package io.github.joht.experiment.jsonb.versioning.person.example.jsonb;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;

import io.github.joht.experiment.jsonb.versioning.VersioningSupport;
import io.github.joht.experiment.jsonb.versioning.person.example.Name;

public class NameAdapter implements JsonbAdapter<Name, JsonObject> {

    private final VersioningSupport<Name> versioning;

    public NameAdapter(JsonbConfig config) {
        Jsonb jsonb = JsonbBuilder.create(config);
        this.versioning = new VersioningSupport<>(Name.class, jsonb::toJson, jsonb::fromJson);
    }

    @Override
    public JsonObject adaptToJson(Name obj) {
        return versioning.reserialize(obj, JsonObject.class);
    }

    @Override
    public Name adaptFromJson(JsonObject obj) {
        return versioning.adaptFromJson(obj.toString());
    }
}