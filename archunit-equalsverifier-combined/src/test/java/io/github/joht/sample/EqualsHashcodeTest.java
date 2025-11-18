package io.github.joht.sample;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import nl.jqno.equalsverifier.api.ConfiguredEqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

/**
 * This test shows, how <i>archunit</i> and <i>equalsverifier</i> can join their forces <br>
 * to verify all equals- and hashCode-Methods inside a package within a single test class.
 * 
 * @author JohT
 */
public class EqualsHashcodeTest {

    private static JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("io.github.joht.sample.basic");/* 1 */

    @Test
    public void testAllEqualsAndHashCodeMethods() {
        ConfiguredEqualsVerifier verifier = EqualsVerifier.configure()
                .usingGetClass()
                .suppress(Warning.STRICT_HASHCODE); /* 2 */

        ArchRuleDefinition.codeUnits().that()
                .haveName("hashCode").or().haveName("equals")
                .should(FulfillEqualsAndHashcodeContract.configuredBy(verifier))
                .check(classes); /* 3 */
    }
}
/*
 * 1. You first need no import the classes, that should be analyzed by ArchUnit. Since it may take a while, it should only be done once per
 * class (hence static).
 * 
 * 2. EqualsVerifier can be configured, if the default settings don’t fit. In this particular case, the hashCode implementation is allowed
 * to skip some fields that are compared inside the equals method, which still meets the contract.
 * 
 * 3. "EqualsAndHashcodeContract" is the most important part. It connects "EqualsVerifier" to "ArchUnit" by extending the abstract class
 * "ArchCondition".
 */