package edu.ktu.screenshotanalyser.checks.semantic;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.screenshotanalyser.TextExtractor.ExtractedText;
import edu.ktu.screenshotanalyser.checks.CheckContext;
import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;

public class SL1_MissingTranslationCheck implements ICheck {

	private final String type = "SL1";

	@Override
	public CheckResult[] analyze(CheckRequest request, CheckContext context) {
		final List<CheckResult> results = new ArrayList<>();

		for (ExtractedText extractedText : request.getExtractedTexts()) {
			final String text = extractedText.getText();
			if (context.isPlaceholder(text)) {
				results.add(CheckResult.Nok(type, String.format("Found missing translation violation for: %s", text),
						request.getOriginalFile().getName()));
			}
		}
		return results.toArray(new CheckResult[0]);
	}

}
