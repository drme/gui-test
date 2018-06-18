package edu.ktu.screenshotanalyser.texts;

import edu.ktu.screenshotanalyser.texts.TextExtractor.TextExtractRequest;
import edu.ktu.screenshotanalyser.texts.TextExtractor.TextExtractResponse;

public interface ITextExtractor {
	TextExtractResponse extract(TextExtractRequest request) throws Throwable;
}
