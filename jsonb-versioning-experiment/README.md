# JSON Binding (JSON-B) Versioning Experiment

This experiment attempts to find a solution for versioning with [JSON Binding][JSONBinding]. When a data object is changed to e.g. add a new field it's JSON representation also changes. In this case it would be ideal that the old and new version can both be deserialized to the new implementation and
therefore avoid breaking changes as long as possible.

[JSONBinding]: https://jakarta.ee/specifications/jsonb/2.0/jakarta-jsonb-spec-2.0.html
