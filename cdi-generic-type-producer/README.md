# cdi-generic-type-producer

Shows how to implement a CDI producer for a generic type with one type argument.
The producer method provides the implementation for all type arguments and is able to gather the argument type from the injection point. The injection point (where `@Inject` is placed) needs a fully qualified type including the specific type argument.
