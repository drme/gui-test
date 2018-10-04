package edu.ktu.screenshotanalyser.checks.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import com.ipeirotis.readability.engine.Readability;
import com.ipeirotis.readability.enums.MetricType;
import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;

public class SS4_UntactfullMessagesCheck implements ICheck
{
	private final String type = "SS4";
	//private final double threshold;
	//private final int minWords;

	//public SU2_TooHardToUnderstandCheck(double threshold, int minWords)
	//{
	//	this.threshold = threshold;
	//	this.minWords = minWords;
	//}

	public SS4_UntactfullMessagesCheck()
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
//			if ("en".equals(readabilityAnalysisLanguage))
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

						
					if ((text.endsWith("!")) && (text.toLowerCase().contains("error")))
					{
					System.out.println("[" + text + "] - no shouting lz");

		//				results.add(CheckResult.Nok(type, String.format("Fairly difficult to read text: %s", text), resourceText.getFile() + "@" + resourceText.getKey(), resourceLanguage));
					}
					}
				}
		//	}
		}

		/*
		 * Document document = new Document(actualText); List<Sentence> sentences = document.sentences(); int sentencesCount = sentences.size(); List<String> words = new ArrayList<>(); sentences.forEach(x -> words.addAll(x.words())); if (words.size() < minWords) { continue; } final double fog = measures.fog(words, sentencesCount); if (fog > this.threshold) { results.add(CheckResult.Nok(type, String.format("Found text violating readalibity index: %s (max: %s) for text: %s", fog, threshold, actualText), resourceText.getFile() + "@" + resourceText.getKey(), resourceLanguage)); }
		 */

		return results.toArray(new CheckResult[0]);
	}
}
