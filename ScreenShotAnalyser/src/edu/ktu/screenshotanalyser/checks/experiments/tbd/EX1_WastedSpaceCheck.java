package edu.ktu.screenshotanalyser.checks.experiments.tbd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;

public class EX1_WastedSpaceCheck// implements IRuleChecker
{
	private final String type = "EX1";
	//private final double threshold;
	//private final int minWords;

	//public SU2_TooHardToUnderstandCheck(double threshold, int minWords)
	//{
	//	this.threshold = threshold;
	//	this.minWords = minWords;
	//}

	public EX1_WastedSpaceCheck()
	{
// TODO: load trained data of images with wasted space...
	}

	
//	@Override
	public CheckResult[] analyze(Object request, AppContext context)
	{
		List<CheckResult> results = new ArrayList<>();

//		clasify each screenshot - has wasted spacem does not have wasted space...
		
		// or just.. well just run opencv flters: grayscale, select contonours regegion, calculate percentage
		
		
		
//		request.
		
		
	/*	for (Entry<String, List<ResourceText>> languageDetails : context.getResources().entrySet())
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
*/

		return results.toArray(new CheckResult[0]);
	}
}
