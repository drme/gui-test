package edu.ktu.screenshotanalyser.checks.semantic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import edu.ktu.screenshotanalyser.checks.experiments.TooHardToUnderstandCheck;

public class TooHardToUnderstandCheckTests extends TooHardToUnderstandCheck
{
	@ParameterizedTest
	@MethodSource
	public void testIsUnderstandable(String message, boolean expected) throws IOException
	{
		boolean actual = isUnderstandable(message);

		Assert.assertEquals(expected, actual);
	}

	private static Stream<Arguments> testIsUnderstandable()
	{
		List<Arguments> testCases = new ArrayList<>();

		testCases.add(Arguments.of("Are you sure?", true));
		testCases.add(Arguments.of("This software is provided AS IS and the author makes NO WARRANTY or representation either express or implied as to its quality, accuracy, or fitness for a particular purpose.", false));

		return testCases.stream();
	}
}
