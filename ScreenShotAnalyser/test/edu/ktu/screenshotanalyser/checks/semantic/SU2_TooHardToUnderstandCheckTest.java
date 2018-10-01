package edu.ktu.screenshotanalyser.checks.semantic;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;

public class SU2_TooHardToUnderstandCheckTest {

	@Test
	public void test() {
		ICheck sut = new SU2_TooHardToUnderstandCheck();
		CheckRequest request = new CheckRequest("testFile.png");
		CheckResult[] results = sut.analyze(request, new AppContext());
		Assert.assertEquals(0, results.length);
	}

	@Test
	public void testCheck() {
		AppContext context = new AppContext();
		context.getResources().put("default",
				Arrays.asList(new ResourceText("test", "Are you sure?", "-"),
						new ResourceText("test2",
								"This software is provided AS IS and the author makes NO WARRANTY or representation either express or implied as to its quality, accuracy, or fitness for a particular purpose.",
								"file1")));
		ICheck sut = new SU2_TooHardToUnderstandCheck();
		CheckResult[] results = sut.analyze(new CheckRequest(null), context);
		Assert.assertEquals(1, results.length);
		Assert.assertEquals("file1@test2", results[0].getFile());
	}

}
