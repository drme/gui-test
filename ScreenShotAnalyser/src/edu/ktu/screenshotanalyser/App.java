package edu.ktu.screenshotanalyser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import edu.ktu.screenshotanalyser.FoundTextsReporter.ReportRequest;
import edu.ktu.screenshotanalyser.ImageContoursProvider.ImageContoursProviderRequest;
import edu.ktu.screenshotanalyser.ImageContoursProvider.ImageContoursResponse;
import edu.ktu.screenshotanalyser.TextExtractor.TextExtractRequest;
import edu.ktu.screenshotanalyser.TextExtractor.TextExtractResponse;
import edu.ktu.screenshotanalyser.checks.CheckContext;
import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.checks.semantic.SL1_MissingTranslationCheck;

public class App {
	private static final Logger logger = Logger.getGlobal();

	static {
		String opencvpath = System.getProperty("user.dir") + "\\lib\\";
		System.load(opencvpath + "opencv_java340.dll");
	}

	private final ITextExtractor textExtractor;
	private final IImageContoursProvider imageContoursProvider;
	private final IReporter reporter;
	private final IFilesProvider filesProvider;

	private final ICheck[] checks = new ICheck[] { new SL1_MissingTranslationCheck() };

	private final ICheckContextProvider checkContextprovider;

	public App() {
		imageContoursProvider = new ImageContoursProvider();
		textExtractor = new TextExtractor(0.60f);
		reporter = new FoundTextsReporter();
		filesProvider = new FilesProvider();
		checkContextprovider = new DummyCheckContextProvider();
	}

	public void work(final String baseDir) {
		final File[] files = this.filesProvider.getFiles(baseDir);

		final CheckContext context = checkContextprovider.getContext(baseDir);

		for (final File file : files) {
			try {
				final ImageContoursResponse imageParserResponse = imageContoursProvider
						.getContours(new ImageContoursProviderRequest(file));

				final TextExtractResponse textExtractResponse = textExtractor
						.extract(new TextExtractRequest(file, imageParserResponse.getBounds()));

				final CheckRequest checkRequest = new CheckRequest(file);
				checkRequest.getContours().addAll(imageParserResponse.getContours());
				checkRequest.getExtractedTexts().addAll(textExtractResponse.getExtractedTexts());
				final List<CheckResult> results = new ArrayList<>();

				for (final ICheck check : this.checks) {
					results.addAll(Arrays.asList(check.analyze(checkRequest, context)));
				}

				final ReportRequest request = new ReportRequest(file);
				request.getContours().addAll(imageParserResponse.getContours());
				request.getExtractedTexts().addAll(textExtractResponse.getExtractedTexts());
				request.getCheckResults().addAll(results);

				reporter.save(request);
			} catch (Throwable e) {
				logger.warning(String.format("Error while analyzing %s file", file.getAbsolutePath()));
			}
		}

	}
}
