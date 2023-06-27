/*

package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import com.aliasi.util.Pair;
import com.github.pemistahl.lingua.api.Language;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;

public class MixedLanguagesStateCheck extends BaseTextRuleCheck implements IStateRuleChecker
{
	public MixedLanguagesStateCheck()
	{
		super(7, "SL2");
	}

	@Override
	public StateCheckResults analyze(State state)
	{
		var uniqueLanguages = new HashMap<Language, List<String>>();
		var messages = state.getActualControls().stream().map(this::getText).filter(p -> isTranslateable(p, state.getAppContext())).map(message -> new Pair<>(message, determineLanguageShort(message))).collect(Collectors.toList());

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
				return null;
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

			if (false == isSpellingCorrect(maxLangauge.getIsoCode639_1().toString(), message.a(), state.getAppContext()))
			{
				allFound = false;
				break;
			}
		}

		if (allFound)
		{
			return null;
		}

		/*
		 * //forEach(message -> mergeLanguages(message, uniqueLanguages)); if (uniqueLanguages.keySet().size() > 1) { var message = "multiple langauges: "; for (var key : uniqueLanguages.keySet()) { message += key + uniqueLanguages.get(key) + "; "; } failures.addFailure(new CheckResult(state, this, message, 1)); }
		 *-/

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

		return new StateCheckResults(state, this, error, 1);
	}

	@Override
	protected String getText(Control control)
	{
		var message = super.getText(control);

		message = message.trim();

		if (message.endsWith("."))
		{
			message = message.substring(0, message.length() - 1);
		}

		return message.trim();
	}

	private void mergeLanguages(String message, HashMap<String, String> uniqueLanguages)
	{
		var languages = determineLanguageShort(message);

		if (languages.size() == 0)
		{
			return;
		}

		/*
		 * var l = languages.get(0).getShortCode(); if (uniqueLanguages.containsKey(l)) { uniqueLanguages.put(l, uniqueLanguages.get(l) + "[" + message.replace("\n", "").replace("\r", "") + "]"); } else { uniqueLanguages.put(l, "[" + message.replace("\n", "").replace("\r", "") + "]"); }
		 *-/
	}

	/*
	 * protected synchronized static List<Language> getLanguageShort(String message) { String code = determineLanguageShort(message); // if (false == code.equals("en")) // { // return new ArrayList<>(); // } return getLanguageByCode(code); }
	 *-/
}

		*/