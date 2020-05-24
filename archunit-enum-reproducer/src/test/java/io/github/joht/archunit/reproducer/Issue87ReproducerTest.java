package io.github.joht.archunit.reproducer;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

/**
 * Reproducer for <a href="https://github.com/TNG/ArchUnit/issues/87">ArchUnit issue 87</a>.
 * 
 * @author JohT
 */
public class Issue87ReproducerTest {

	private static JavaClasses classes = new ClassFileImporter()
			.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
			.importPackages("io.github.joht.archunit.reproducer");

	@Test
	//Note: Since archunit 0.14.0 this test succeeds.
	public void testNoCallsToDeprecatedMethods() {
		ArchRuleDefinition.classes().that().resideInAnyPackage("..reproducer..")
				.should().onlyCallMethodsThat(are(not(annotatedWith(Deprecated.class))))
				.check(classes);
	}
}