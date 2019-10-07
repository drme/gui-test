package edu.ktu.screenshotanalyser.checks.semantic;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.checks.experiments.MissingTranslationCheck;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.rules.checkers.CheckRequest;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;
import edu.ktu.screenshotanalyser.rules.checkers.IRuleChecker;
import edu.ktu.screenshotanalyser.texts.TextExtractor.ExtractedText;
import net.sourceforge.tess4j.Word;

public class SL1_MissingTranslationCheckTest {

	@Test
	public void test() throws IOException {
		IRuleChecker sut = new MissingTranslationCheck();
		CheckRequest request = new CheckRequest("testFile.png");
		CheckResult[] results = sut.analyze(request, new AppContext(null, null, null, null));
		Assert.assertEquals(0, results.length);
	}

	@Test
	public void testCheck() throws IOException {
		AppContext context =	new AppContext(null, null, null, null);
		context.getKeys().add("test_3");
		IRuleChecker sut = new MissingTranslationCheck();
		CheckRequest request = new CheckRequest("testFile.png");
		request.getExtractedTexts().add(new ExtractedText(new Word[0], "test_3", new Rect()));
		CheckResult[] results = sut.analyze(request, context);
		Assert.assertEquals(1, results.length);
	}
}
