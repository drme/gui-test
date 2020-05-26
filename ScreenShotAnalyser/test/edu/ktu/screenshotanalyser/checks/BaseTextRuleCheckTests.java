package edu.ktu.screenshotanalyser.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class BaseTextRuleCheckTests extends BaseTextRuleCheck
{
	protected BaseTextRuleCheckTests()
	{
		super(-1, "");
	}
	
	@ParameterizedTest
	@MethodSource
	public void testIsSimillar(String left, String right, boolean expected)
	{
		Assert.assertEquals(expected, isSimillar(left, right));
	}
	
	private static Stream<Arguments> testIsSimillar()
	{
		List<Arguments> testCases = new ArrayList<>();

		//testCases.add(Arguments.of("a", "b", false));
		testCases.add(Arguments.of("Signin", "Sign in", true));

		return testCases.stream();
	}	
}
