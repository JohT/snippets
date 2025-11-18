package io.github.joht.sample;

import org.junit.jupiter.api.Disabled;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import nl.jqno.equalsverifier.api.ConfiguredEqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;

/**
 * Link between "ArchUnit" and "EqualsVerifier".
 * 
 * @author JohT
 */
@Disabled
class FulfillEqualsAndHashcodeContract extends ArchCondition<JavaCodeUnit> {

    private final ConfiguredEqualsVerifier config;

    public static final ArchCondition<JavaCodeUnit> configuredBy(ConfiguredEqualsVerifier config) {
        return new FulfillEqualsAndHashcodeContract(config);
    }

    private FulfillEqualsAndHashcodeContract(ConfiguredEqualsVerifier config) {
        super("fulfills the equals and hashCode contract");
        this.config = config;
    }

    @Override
    public void check(JavaCodeUnit codeUnit, ConditionEvents events) {
        Class<?> classToTest = classForName(codeUnit.getOwner().getName());
        EqualsVerifierReport report = config.forClass(classToTest).report(); /* 1 */
        events.add(eventFor(report, codeUnit.getOwner())); /* 2 */
    }

    private static SimpleConditionEvent eventFor(EqualsVerifierReport report, JavaClass owner) {
        return new SimpleConditionEvent(owner, report.isSuccessful(), report.getMessage());
    }

    private static Class<?> classForName(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return "FulfillEqualsAndHashcodeContract [config=" + config + "]";
    }
}
/*
 * 1. The exchangeable "ConfiguredEqualsVerifier" is used to generate the report for equals and hashCode methods. This is done for the
 * JavaCodeUnit, that is currently selected by "ArchUnit".
 * 
 * 2. The report of the "EqualsVerifier" is converted to a "SimpleConditionEvent" and added to the other events. If all checks are
 * successful, the test will pass. If one of the CodeUnits do not fulfill the contract, they will be listed in the message of the failed
 * test.
 */