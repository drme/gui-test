package edu.ktu.screenshotanalyser.checks.semantic;

import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Rect;

import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.texts.TextExtractor.ExtractedText;
import net.sourceforge.tess4j.Word;

public class SL1_MissingTranslationCheckTest {

	@Test
	public void test() {
		ICheck sut = new SL1_MissingTranslationCheck();
		CheckRequest request = new CheckRequest("testFile.png");
		CheckResult[] results = sut.analyze(request, new AppContext());
		Assert.assertEquals(0, results.length);
	}

	@Test
	public void testCheck() {
		AppContext context =	new AppContext();
		context.getKeys().add("test_3");
		ICheck sut = new SL1_MissingTranslationCheck();
		CheckRequest request = new CheckRequest("testFile.png");
		request.getExtractedTexts().add(new ExtractedText(new Word[0], "test_3", new Rect()));
		CheckResult[] results = sut.analyze(request, context);
		Assert.assertEquals(1, results.length);
	}
}
