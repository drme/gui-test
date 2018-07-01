package edu.ktu.screenshotanalyser.checks.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.tudarmstadt.ukp.dkpro.core.readability.measure.ReadabilityMeasures;
import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class SU2_TooHardToUnderstandCheck implements ICheck {

	private final String type = "SU2";
	private final double threshold;
	private final int minWords;

	public SU2_TooHardToUnderstandCheck(double threshold, int minWords) {
		this.threshold = threshold;
		this.minWords = minWords;
	}

	public SU2_TooHardToUnderstandCheck() {
		this(13d, 10);
	}

	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		final List<CheckResult> results = new ArrayList<>();

		for (final Entry<String, List<ResourceText>> languageDetails : context.getResources().entrySet()) {
			final String resourceLanguage = languageDetails.getKey();
			final String readabilityAnalysisLanguage = resourceLanguage.equals("default") ? "en" : resourceLanguage;

			final ReadabilityMeasures measures = new ReadabilityMeasures(readabilityAnalysisLanguage);
			for (final ResourceText resourceText : languageDetails.getValue()) {

				final String actualText = resourceText.getValue();

				if (actualText == null) {
					continue;
				}
				Document document = new Document(actualText);
				List<Sentence> sentences = document.sentences();
				int sentencesCount = sentences.size();
				List<String> words = new ArrayList<>();
				sentences.forEach(x -> words.addAll(x.words()));
				if (words.size() < minWords) {
					continue;
				}
				final double fog = measures.fog(words, sentencesCount);
				if (fog > this.threshold) {
					results.add(CheckResult.Nok(type,
							String.format("Found text violating readalibity index: %s (max: %s) for text: %s", fog,
									threshold, actualText),
							resourceText.getFile() + "@" + resourceText.getKey(), resourceLanguage));
				}
			}
		}

		return results.toArray(new CheckResult[0]);
	}

}
