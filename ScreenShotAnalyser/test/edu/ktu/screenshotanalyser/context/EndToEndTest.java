package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;
import edu.ktu.screenshotanalyser.Screen;
import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.MultiChecker;
import edu.ktu.screenshotanalyser.checks.experiments.TS2_UnreadableTextCheck;
import edu.ktu.screenshotanalyser.contours.ImageContoursProvider;
import edu.ktu.screenshotanalyser.contours.ImageContoursProvider.ImageContoursProviderRequest;
import edu.ktu.screenshotanalyser.contours.ImageContoursProvider.ImageContoursResponse;
import edu.ktu.screenshotanalyser.reporters.FoundTextsOnImagesReporter2;
import edu.ktu.screenshotanalyser.texts.TextExtractor;
import edu.ktu.screenshotanalyser.texts.TextExtractor.TextExtractRequest;
import edu.ktu.screenshotanalyser.texts.TextExtractor.TextExtractResponse;

public class EndToEndTest {

	@Test
	public void test() throws AndrolibException, DirectoryException, IOException {

		String dir = "./samples2";
		String resultsDir = "./results";

		ImageContoursProvider contoursProvider = new ImageContoursProvider();
		TextExtractor extractor = new TextExtractor(0.65f, "./tessdata", "nor");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		File main = new File(dir);
		File[] apkFiles = main.listFiles(x -> x.getName().endsWith(".apk") && x.isFile());
		System.out.println("FOUND " + apkFiles.length);
		for (File apk : apkFiles) {
			System.out.println("APK: " + apk);
			String projectApkName = apk.getName();
			File[] foundResultsForApk = main.listFiles(x -> x.getName().startsWith(projectApkName) && x.isDirectory());
			File baseProjectOutDir = Paths.get(resultsDir, projectApkName).toFile();
			File apkExtractedResources = Paths.get(baseProjectOutDir.getAbsolutePath(), "resources").toFile();

			if (!apkExtractedResources.exists()) {
				ApkDecoder decoder = new ApkDecoder();
				decoder.setApkFile(apk);

				decoder.setOutDir(apkExtractedResources);
				decoder.decode();
				decoder.close();
			}
			List<Screen> alLScreens = new LinkedList<>();
			for (File foundDir : foundResultsForApk) {
				String config = foundDir.getName().replaceAll(projectApkName, "").replaceFirst("_", "");
				String device = config.split("_API")[0];
				File[] screenFiles = new File(foundDir, "states").listFiles(x -> x.getName().startsWith("screen_"));

				for (File screen : screenFiles) {

					File analyzedScreen = Paths
							.get(baseProjectOutDir.getAbsolutePath(), config, screen.getName().replace(".png", ".json"))
							.toFile();
					File tempScreen = Paths.get(baseProjectOutDir.getAbsolutePath(), config, screen.getName()).toFile();
					boolean force = false;
					if (!analyzedScreen.exists() || force) {
						analyzedScreen.getParentFile().mkdirs();
						String actualTextFile = screen.getAbsolutePath().replace("screen_", "text_").replace(".png",
								".txt");
						try {

							ImageContoursResponse contours = contoursProvider
									.getContours(new ImageContoursProviderRequest(screen));
							TextExtractResponse response = extractor
									.extract(new TextExtractRequest(screen, contours.getBounds()));
							edu.ktu.screenshotanalyser.Screen screenObj = new Screen();
							screenObj.extractedTexts = response.getExtractedTexts();
							screenObj.shortFileName = screen.getName();
							screenObj.appName = projectApkName;
							screenObj.config = config;
							screenObj.device = device;
							screenObj.originalFile = screen;
							screenObj.contours = contours.getContours();
							screenObj.width = contours.getWidth();
							screenObj.height = contours.getHeight();
							screenObj.actualTexts = Files.lines(Paths.get(actualTextFile)).collect(Collectors.toList())
									.toArray(new String[0]);
							FileUtils.write(analyzedScreen, gson.toJson(screenObj));
							new FoundTextsOnImagesReporter2().save(screenObj, tempScreen.getAbsolutePath());

						} catch (Throwable e) {
							e.printStackTrace();
						}

					}
					if (analyzedScreen.exists()) {
						Screen e = gson.fromJson(new String(Files.readAllBytes(analyzedScreen.toPath())), Screen.class);
						alLScreens.add(e);
					}

				}

			}

			DefaultContextProvider contextProvider = new DefaultContextProvider();
			AppContext context = contextProvider.getContext(apkExtractedResources.getAbsolutePath());
			MultiChecker checker = new MultiChecker(new TS2_UnreadableTextCheck());

			for (Screen sc : alLScreens) {
				CheckRequest request = new CheckRequest(sc.originalFile.getAbsolutePath(), sc.device, sc.width,
						sc.height);
				request.getContours().addAll(sc.contours);
				request.getExtractedTexts().addAll(sc.extractedTexts);

				CheckResult[] results = checker.analyze(request, context);
				for (CheckResult r : results) {
					System.out.println(r.getMessage() + " " + r.getFile());
				}

			}

		}
	}

}
