package io.github.joht.archunit.reproducer;

import java.util.Arrays;

public class ClassThatCallsEnumValues {

	public void callsEnumValues() {
		System.out.println(Arrays.toString(SimpleEnum.values()));
	}
	
}
