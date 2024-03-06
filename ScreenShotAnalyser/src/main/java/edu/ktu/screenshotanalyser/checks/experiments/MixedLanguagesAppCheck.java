package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import com.aliasi.util.Pair;
import com.github.pemistahl.lingua.api.Language;
import edu.ktu.screenshotanalyser.checks.AppCheckResults;
import edu.ktu.screenshotanalyser.checks.AppDefectAnnotation;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;

public class MixedLanguagesAppCheck extends  BaseTextRuleCheck implements IAppRuleChecker
{
	public MixedLanguagesAppCheck()
	{
		super(30, "MixedLanguagesApp");
	}

	@Override
	public void analyze(AppContext appContext, AppCheckResults results)
	{
		//var languages = new HashSet<Language>();
		var deviceStates = appContext.getStates().stream().collect(Collectors.groupingBy(State::getTestDevice));

		for (var testDevice : deviceStates.keySet())
		{
			var m1 = deviceStates.get(testDevice).stream();
			var uniqueLanguages = new HashMap<Language, List<String>>();
			var messages = m1.map(this::getMessage).filter(p -> isTranslateable(p, appContext)).map(message -> new Pair<>(message, determineLanguageShort(message))).toList();

			for (var message : messages)
			{
				for (var language : message.b().keySet())
				{
					var prob = message.b().get(language);

					if (prob > 0.7)
					{
						if (uniqueLanguages.containsKey(language))
						{
							var m = uniqueLanguages.get(language);

							m.add(message.a());

							uniqueLanguages.put(language, m);
						}
						else
						{
							var m = new ArrayList<String>();

							m.add(message.a());

							uniqueLanguages.put(language, m);
						}
					}
				}
			}

			for (var language : uniqueLanguages.keySet())
			{
				var languageMessages = uniqueLanguages.get(language);

				if (languageMessages.size() == messages.size())
				{
					return;
				}
			}

			var maxMessages = -1;
			Language maxLangauge = null;

			for (var language : uniqueLanguages.keySet())
			{
				var m = uniqueLanguages.get(language);

				if (m.size() >= maxMessages)
				{
					maxMessages = m.size();
					maxLangauge = language;
				}
			}

			var allFound = true;
			var ok = uniqueLanguages.get(maxLangauge);

			for (var message : messages)
			{
				if (ok.contains(message.a()))
				{
					continue;
				}

				if (!isSpellingCorrect(maxLangauge.getIsoCode639_1().toString(), message.a(), appContext))
				{
					allFound = false;
					break;
				}
			}

			if (allFound)
			{
				return;
			}

			/*
			 * //forEach(message -> mergeLanguages(message, uniqueLanguages)); if (uniqueLanguages.keySet().size() > 1) { var message = "multiple langauges: "; for (var key : uniqueLanguages.keySet()) { message += key + uniqueLanguages.get(key) + "; "; } failures.addFailure(new CheckResult(state, this, message, 1)); }
			 */

			var error = "";

			for (var message : messages)
			{
				var maxScore = -1.0;
				Language language = null;

				for (var l : message.b().keySet())
				{
					var p = message.b().get(l);

					if (p >= maxScore)
					{
						language = l;
						maxScore = p;
					}
				}

				error += message.a().replace("\n", " ").replace("\r", " ") + "[" + language.getIsoCode639_1() + " " + maxScore + "] ";
			}

			results.addAnnotation(new AppDefectAnnotation(this, testDevice.name + ": " + error));
		}

	/*	
		var languages = new HashSet<Language>();
		Map<TestDevice, List<State>> deviceStates = appContext.getStates().stream().collect(Collectors.groupingBy(State::getTestDevice));
		for (var testDevice : deviceStates.keySet())
		{
			for (var state : deviceStates.get(testDevice))
			{
				var stateLanguage = determineStateLanguage(state);
				if (stateLanguage.size() > 0)
				{
					languages.add(stateLanguage.get(0));
				}
			}
			if (languages.size() > 1)
			{
				failures.addFailure(new CheckResult(appContext, this, "multiple langs: " + testDevice.folder.getName() + ": " + languages.toString()));
			}
		}
*/		 
	}
	
	private String getMessage(State state)
	{
		return state.getActualControls().stream().map(this::getText).filter(p -> isTranslateable(p, state.getAppContext())).collect(Collectors.joining(". "));
	}
	
	/*
	private List<org.languagetool.Language> determineStateLanguage(State state)
	{
		var allTexts = state.getActualControls().stream().map(this::getText).filter(p -> isTranslateable(p, state.getAppContext())).collect(Collectors.joining(". "));

		return getLanguage(allTexts);
	} */
}
