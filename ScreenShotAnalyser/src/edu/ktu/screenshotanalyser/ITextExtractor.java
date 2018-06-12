package edu.ktu.screenshotanalyser;

import edu.ktu.screenshotanalyser.TextExtractor.TextExtractRequest;
import edu.ktu.screenshotanalyser.TextExtractor.TextExtractResponse;

public interface ITextExtractor {
	TextExtractResponse extract(TextExtractRequest request) throws Throwable;
}
