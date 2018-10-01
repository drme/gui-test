package edu.ktu.screenshotanalyser.checks.semantic;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.texts.TextExtractor.ExtractedText;

public class SL1_MissingTranslationCheck implements ICheck {

	private final String type = "SL1";

	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		final List<CheckResult> results = new ArrayList<>();

		for (ExtractedText extractedText : request.getExtractedTexts()) {
			final String text = extractedText.getText();
			if (context.isPlaceholder(text)) {
				results.add(CheckResult.Nok(type, String.format("Found missing translation violation for: %s", text),
						request.getOriginalFile(), null));
			}
		}
		return results.toArray(new CheckResult[0]);
	}

}
