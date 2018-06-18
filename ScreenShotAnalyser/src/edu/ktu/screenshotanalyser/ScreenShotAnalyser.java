package edu.ktu.screenshotanalyser;

import java.util.List;

import com.sampullara.cli.Args;

public class ScreenShotAnalyser {
	public static void main(String[] args) throws Throwable {

		AnalyzerSettings request = new AnalyzerSettings();
		List<String> unparsed = Args.parseOrExit(request, args);
		System.load(request.getCvLibrary());
		ImageTextsExtractor extractor = ImageTextsExtractor.fromRequest(request);
		extractor.extract(new AnalyzerRequest(request.getInputDir(), request.getOutputDir()));

	};

};
