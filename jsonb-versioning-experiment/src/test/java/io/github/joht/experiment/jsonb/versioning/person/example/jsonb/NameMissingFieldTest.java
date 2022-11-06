package io.github.joht.experiment.jsonb.versioning.person.example.jsonb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.joht.experiment.jsonb.versioning.person.example.Name;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

class NameMissingFieldTest {

    private final Jsonb jsonb = JsonbBuilder.create();

    @Test
    @DisplayName("A missing field in the JSON structure will be deserialized as null by default")
    void deserializeMissingFieldWithNull() {
        String jsonWithMissingField = "{\"forename\":\"John\",\"surename\":\"Doe\"}";
        Name deserializedName = jsonb.fromJson(jsonWithMissingField, Name.class);
        
        assertNull(deserializedName.title);
        
        assertEquals(deserializedName.forename, "John");
        assertEquals(deserializedName.surename, "Doe");
    }

}
