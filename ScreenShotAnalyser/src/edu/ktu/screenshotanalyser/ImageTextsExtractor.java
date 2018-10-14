package edu.ktu.screenshotanalyser;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.ktu.screenshotanalyser.reporters.FoundTextsOnImagesReporter.AppExtractInfo;
import edu.ktu.screenshotanalyser.contours.IImageContoursProvider;
import edu.ktu.screenshotanalyser.contours.ImageContoursProvider;
import edu.ktu.screenshotanalyser.contours.ImageContoursProvider.ImageContoursProviderRequest;
import edu.ktu.screenshotanalyser.contours.ImageContoursProvider.ImageContoursResponse;
import edu.ktu.screenshotanalyser.reporters.IReporter;
import edu.ktu.screenshotanalyser.reporters.MultIReporter;
import edu.ktu.screenshotanalyser.texts.ITextExtractor;
import edu.ktu.screenshotanalyser.texts.TextExtractor;
import edu.ktu.screenshotanalyser.texts.TextExtractor.TextExtractRequest;
import edu.ktu.screenshotanalyser.texts.TextExtractor.TextExtractResponse;

public class ImageTextsExtractor {
	private static final Logger logger = Logger.getGlobal();
	private final IImageContoursProvider imageContoursProvider = new ImageContoursProvider();
	private final ITextExtractor textExtractor;
	private final IReporter reporter;
	private final IFilesProvider<AppImageFiles> filesProvider = new DroidBotFilesProvider();

	public ImageTextsExtractor(ITextExtractor textExtractor, IReporter reporter) {
		this.textExtractor = textExtractor;
		this.reporter = reporter;
	}

	public static ImageTextsExtractor fromRequest(AnalyzerSettings request) {
		return new ImageTextsExtractor(
				new TextExtractor(request.getPrecision(), request.getTessDataFolder(), request.getLanguage()),
				new MultIReporter(request.getOutputDir()));
	}

	public void extract(final AnalyzerRequest analyzerRequest) {
		logger.log(Level.INFO, String.format("Working on: %s", analyzerRequest));
		AppImageFiles[] appImageFiles = filesProvider.getFiles(analyzerRequest.getBaseDir());

		for (final AppImageFiles appImageFile : appImageFiles) {
			try {
				final File[] files = appImageFile.getFiles();
				for (final File file : files) {

					try {

						logger.log(Level.INFO, "Analyzing: " + file.getAbsolutePath());
						final ImageContoursResponse imageParserResponse = imageContoursProvider
								.getContours(new ImageContoursProviderRequest(file));

						final TextExtractResponse textExtractResponse = textExtractor
								.extract(new TextExtractRequest(file, imageParserResponse.getBounds()));

						final AppExtractInfo request = new AppExtractInfo(file, appImageFile.getBaseDirName(),
								appImageFile.getProjectName(), appImageFile.getConfig());
						request.getContours().addAll(imageParserResponse.getContours());
						request.getExtractedTexts().addAll(textExtractResponse.getExtractedTexts());
						reporter.save(request);
					} catch (Throwable e) {
						logger.log(Level.WARNING,
								String.format("Error while analyzing %s file", file.getAbsolutePath()), e);
					}

				}

			} catch (Throwable e) {
				logger.log(Level.WARNING, String.format("Error while analyzing: %s", appImageFile), e);
			}
		}
	}
}
