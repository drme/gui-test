package edu.ktu.screenshotanalyser.checks;

import java.io.IOException;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

public class SystemContext
{
	public SystemContext() throws IOException
	{
		this.categorizer = new LanguageDetectorME(new LanguageDetectorModel(getClass().getResourceAsStream("/langdetect-183.bin")));
	}

	public String predictLanguage(String message)
	{
		var bestLanguage = this.categorizer.predictLanguage(message);
		// System.out.println("Best language: " + bestLanguage.getLang());
		// System.out.println("Best language confidence: " + bestLanguage.getConfidence());

		// Get an array with the most probable languages
		for (var language : categorizer.predictLanguages(message))
		{
			// System.out.println("language: " + language.getLang());
			// System.out.println("language confidence: " + language.getConfidence());
		}

		return bestLanguage.getLang();
	}

	private final LanguageDetector categorizer;
}
