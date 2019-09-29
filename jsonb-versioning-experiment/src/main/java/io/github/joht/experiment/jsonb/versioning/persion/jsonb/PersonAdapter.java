package io.github.joht.experiment.jsonb.versioning.persion.jsonb;

import javax.json.JsonObject;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;

import io.github.joht.experiment.jsonb.versioning.persion.VersioningAdapter;
import io.github.joht.experiment.jsonb.versioning.persion.api.Person;

public class PersonAdapter implements JsonbAdapter<Person, JsonObject> {

    private final VersioningAdapter<Person> versioning;

    public PersonAdapter(JsonbConfig config) {
        this.versioning = new VersioningAdapter<>(config, Person.class);
    }

    @Override
    public JsonObject adaptToJson(Person obj) {
        return versioning.adaptToJson(obj);
    }

    @Override
    public Person adaptFromJson(JsonObject obj) {
        return versioning.adaptFromJson(obj);
    }
}