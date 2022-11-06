# JSON Binding (JSON-B) Versioning Experiment

This experiment attempts to find a solution for versioning with [JSON Binding][JSONBinding]. When a data object is changed, it's JSON representation changes too. The easiest case is a new field that can be empty. Missing fields in the JSON structure will be deserialized to `null`. A more complex case is when fields get grouped together into a new data object. This results in nested JSON objects. The old JSON structure without the nested object can't be deserialized into the new object without further ado.

In this case it would be ideal that the old and new version can both be deserialized to the new implementation and therefore avoid breaking changes as long as possible. The idea behind this is borrowed from "Upcaster" in Event Sourcing systems.

It would also be great to do this within the data objects that changed. In an object oriented manner they are responsible for their JSON representation. They shouldn't depend on a JSON library, so they should only describe their representation but not actually implement Serialization and Deserialization. This stands of course in clear contrast to the commonly used approach to generate these data objects using an [Open API Generator][OpenApiGenerator]. These generated classes are better seen as data structures and less as objects in an object oriented manner. How they can be made "lenient" to changes isn't addressed in this experiment.

Since JSON Binding standard implementation `yasson v1.0.8` it isn't possible to register a `JsonbDeserializer<Object>` for all objects. In this example, the data objects were changes to implement `Serializable` to have one common type for all of them. This isn't a good solution
since the objects don't need to be `Serializable`. Its just used to "mark" them with a standard Java interface.

What could also be tried out in future:

- Custom e.g. `JsonVersionable` Interface to get rid of reflection and type safety.
- Solution for generated code by e.g. an Open API generator.

[JSONBinding]: https://jakarta.ee/specifications/jsonb/2.0/jakarta-jsonb-spec-2.0.html
[OpenApiGenerator]: https://github.com/OpenAPITools/openapi-generator
