package edu.ktu.screenshotanalyser.checks.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import com.ipeirotis.readability.engine.Readability;
import com.ipeirotis.readability.enums.MetricType;
import de.tudarmstadt.ukp.dkpro.core.readability.measure.ReadabilityMeasures;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;
import edu.ktu.screenshotanalyser.rules.checkers.CheckRequest;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;
import edu.ktu.screenshotanalyser.rules.checkers.IRuleChecker;

/**
 * https://ipeirotis-hrd.appspot.com/
 */
public class SU2_TooHardToUnderstandCheck implements IRuleChecker
{
	private final String type = "SU2";
	//private final double threshold;
	//private final int minWords;

	//public SU2_TooHardToUnderstandCheck(double threshold, int minWords)
	//{
	//	this.threshold = threshold;
	//	this.minWords = minWords;
	//}

	public SU2_TooHardToUnderstandCheck()
	{
		//this(13d, 10);
	}

	
	//TODO run on texts extracted from screenshots also, skip garbage from resource files like $1 place-holders
	
	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context)
	{
		List<CheckResult> results = new ArrayList<>();

		for (Entry<String, List<ResourceText>> languageDetails : context.getResources().entrySet())
		{
			String resourceLanguage = languageDetails.getKey();
			String readabilityAnalysisLanguage = resourceLanguage.equals("default") ? "en" : resourceLanguage;

			// no indexes for other languages..
			if ("en".equals(readabilityAnalysisLanguage))
			{
				//ReadabilityMeasures measures = new ReadabilityMeasures(readabilityAnalysisLanguage);

				for (ResourceText resourceText : languageDetails.getValue())
				{
					String text = resourceText.getValue();

					if ((text == null) || (text.isEmpty()))
					{
						continue;
					}

					text = text.trim();

					Readability readability = new Readability(text);

					double characters = readability.getMetric(MetricType.CHARACTERS);
					double words = readability.getMetric(MetricType.WORDS);
					double sentences = readability.getMetric(MetricType.SENTENCES);
					
					if ((characters > 0) && (words > 1) && (sentences > 0)) // no 1 word sentences are allowed
					{
					double ari = readability.getMetric(MetricType.ARI);
					double colemanLiau = readability.getMetric(MetricType.COLEMAN_LIAU);
					double complexWords = readability.getMetric(MetricType.COMPLEXWORDS);
					double fleschKincaid = readability.getMetric(MetricType.FLESCH_KINCAID);
					double flecshReading = readability.getMetric(MetricType.FLESCH_READING);
					double gunningFog = readability.getMetric(MetricType.GUNNING_FOG);
					double smog = readability.getMetric(MetricType.SMOG);
					double smogIndex = readability.getMetric(MetricType.SMOG_INDEX);
					double syllabes = readability.getMetric(MetricType.SYLLABLES);

					// TODO: skip garbage texts, combine index to somethig useable... select index that wprks with single sentence...
					
					if ((flecshReading < 70) && (smogIndex > 10))
					{
						System.out.println("[" + text + "] - FleschReadingEase: " + flecshReading + ", ari: " + ari);

						results.add(CheckResult.Nok(type, String.format("Fairly difficult to read text: %s", text), resourceText.getFile() + "@" + resourceText.getKey(), resourceLanguage));
					}
					}
				}
			}
		}

		/*
		 * Document document = new Document(actualText); List<Sentence> sentences = document.sentences(); int sentencesCount = sentences.size(); List<String> words = new ArrayList<>(); sentences.forEach(x -> words.addAll(x.words())); if (words.size() < minWords) { continue; } final double fog = measures.fog(words, sentencesCount); if (fog > this.threshold) { results.add(CheckResult.Nok(type, String.format("Found text violating readalibity index: %s (max: %s) for text: %s", fog, threshold, actualText), resourceText.getFile() + "@" + resourceText.getKey(), resourceLanguage)); }
		 */

		return results.toArray(new CheckResult[0]);
	}
}
