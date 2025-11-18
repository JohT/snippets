package io.github.joht.sample;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.ConfiguredEqualsVerifier;

/**
 * This test shows, how <i>archunit</i> and <i>equalsverifier</i> can join their forces <br>
 * to specify rules for a package regarding equals and hashCode.
 * 
 * @author JohT
 */
public class EqualsHashcodeSpecificationTest {

    private static JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("io.github.joht.sample");

    @Test
    @DisplayName("entities are considered equal, if their id are equal") /* 1 */
    public void entitiesAreConsideredEqualIfTheirIdAreEqual() {
        ConfiguredEqualsVerifier verifier = EqualsVerifier.configure()
                .usingGetClass()
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED);

        ArchRuleDefinition.methods().that()
                .haveName("hashCode").or().haveName("equals")
                .and().areDeclaredInClassesThat().haveSimpleNameContaining("Entity") /* 2 */
                .should(FulfillEqualsAndHashcodeContract.configuredBy(verifier)) /* 3 */
                .check(classes);
    }

    @Test
    @DisplayName("value objects are only considered equal, if all of their fields are equal")
    public void valueObjectsAreOnlyConsideredEqualIfAllOfTheirFieldsAreEqual() {
        ConfiguredEqualsVerifier verifier = EqualsVerifier.configure()
                .usingGetClass()
                .suppress(Warning.STRICT_HASHCODE);

        ArchRuleDefinition.methods().that()
                .haveName("hashCode").or().haveName("equals")
                .and().areDeclaredInClassesThat().haveSimpleNameContaining("Value")
                .should(FulfillEqualsAndHashcodeContract.configuredBy(verifier))
                .check(classes);
    }
}