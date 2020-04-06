# Reproducer for the ArchUnit issue 87
This small project reproduces [ArchUnit issue #87](https://github.com/TNG/ArchUnit/issues/87) that leads to the following false-positive method rule violation:

``` 
Architecture Violation [Priority: MEDIUM] - Rule 'classes that reside in any package ['..reproducer..'] should only call methods that are not annotated with @Deprecated' was violated (1 times):
Method <io.github.joht.archunit.reproducer.SimpleEnum.values()> calls method <[Lio.github.joht.archunit.reproducer.SimpleEnum;.clone()> in (SimpleEnum.java:3)
```