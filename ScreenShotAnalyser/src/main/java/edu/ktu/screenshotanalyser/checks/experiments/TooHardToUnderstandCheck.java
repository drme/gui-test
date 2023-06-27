package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.List;
import com.ipeirotis.readability.engine.Readability;
import com.ipeirotis.readability.enums.MetricType;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.IResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;

/**
 * https://ipeirotis-hrd.appspot.com/
 */
public class TooHardToUnderstandCheck extends BaseTextRuleCheck implements IStateRuleChecker, IAppRuleChecker
{
	//private final double threshold;
	//private final int minWords;

	//public SU2_TooHardToUnderstandCheck(double threshold, int minWords)
	//{
	//	this.threshold = threshold;
	//	this.minWords = minWords;
	//}

	public TooHardToUnderstandCheck()
	{
		super(-1, "SU2");

		// this(13d, 10);
	}
	
	//TODO run on texts extracted from screenshots also, skip garbage from resource files like $1 place-holders
	//@Override
	public StateCheckResults[] analyze(Object request, AppContext context)
	{
		List<StateCheckResults> results = new ArrayList<>();
		/*
		 * for (Entry<String, List<ResourceText>> languageDetails : context.getResources().entrySet()) { String resourceLanguage = languageDetails.getKey(); String readabilityAnalysisLanguage = resourceLanguage.equals("default") ? "en" : resourceLanguage; // no indexes for other languages.. if ("en".equals(readabilityAnalysisLanguage)) { //ReadabilityMeasures measures = new ReadabilityMeasures(readabilityAnalysisLanguage); for (ResourceText resourceText : languageDetails.getValue()) { String text = resourceText.getValue(); is REadable.TooHardToUnderstandCheck.class.9 System.out.println("[" + text + "] - FleschReadingEase: " + flecshReading + ", ari: " + ari); results.add(CheckResult.Nok(type, String.format("Fairly difficult to read text: %s", text), resourceText.getFile() + "@" + resourceText.getKey(), resourceLanguage)); } } }
		 */
		/*
		 * Document document = new Document(actualText); List<Sentence> sentences = document.sentences(); int sentencesCount = sentences.size(); List<String> words = new ArrayList<>(); sentences.forEach(x -> words.addAll(x.words())); if (words.size() < minWords) { continue; } final double fog = measures.fog(words, sentencesCount); if (fog > this.threshold) { results.add(CheckResult.Nok(type, String.format("Found text violating readalibity index: %s (max: %s) for text: %s", fog, threshold, actualText), resourceText.getFile() + "@" + resourceText.getKey(), resourceLanguage)); }
		 */

		return results.toArray(new StateCheckResults[0]);
	}

//	@Override
	public StateCheckResults analyze(State state)
	{
		var messages = new ArrayList<String>();

		state.getActualControls().stream().filter(p -> null != p.getText()).forEach(p -> messages.add(p.getText()));
		state.getActualControls().stream().filter(p -> null != p.getContentDescription()).forEach(p -> messages.add(p.getContentDescription()));

		var errors = "";

		for (var message : messages)
		{
			if (false == isUnderstandable(message))
			{
				errors += "[" + message.replace('\n', ' ').replace('\r', ' ') + "] ";
			}
		}

		errors = errors.trim();

		if (errors.length() > 0)
		{
			// ???
			return null;//new StateCheckResults(state, this, "hard 2 understand " + errors, errors.length());
		}
		
		return null;
	}

	@Override
	public void analyze(AppContext appContext, IResultsCollector failures)
	{
	}
	
	protected boolean isUnderstandable(String text)
	{
		if (text == null)
		{
			return true;
		}

		text = text.trim();

		if (text.isEmpty())
		{
			return true;
		}

		var readability = new Readability(text);
		var characters = readability.getMetric(MetricType.CHARACTERS);
		var words = readability.getMetric(MetricType.WORDS);
		var sentences = readability.getMetric(MetricType.SENTENCES);

		if ((characters > 0) && (words > 1) && (sentences > 0)) // no 1 word sentences are allowed
		{
			var ari = readability.getMetric(MetricType.ARI);
			var colemanLiau = readability.getMetric(MetricType.COLEMAN_LIAU);
			var complexWords = readability.getMetric(MetricType.COMPLEXWORDS);
			var fleschKincaid = readability.getMetric(MetricType.FLESCH_KINCAID);
			var flecshReading = readability.getMetric(MetricType.FLESCH_READING);
			var gunningFog = readability.getMetric(MetricType.GUNNING_FOG);
			var smog = readability.getMetric(MetricType.SMOG);
			var smogIndex = readability.getMetric(MetricType.SMOG_INDEX);
			var syllabes = readability.getMetric(MetricType.SYLLABLES);

			// TODO: skip garbage texts, combine index to somethig useable... select index that wprks with single sentence...

			if ((flecshReading < 70) && (smogIndex > 10))
			{
				return false;
			}
		}

		return true;
	}
}
