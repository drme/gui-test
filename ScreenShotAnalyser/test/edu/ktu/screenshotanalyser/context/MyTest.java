package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.ktu.screenshotanalyser.contours.ImageContoursProvider;
import edu.ktu.screenshotanalyser.contours.ImageContoursProvider.ImageContoursProviderRequest;
import edu.ktu.screenshotanalyser.contours.ImageContoursProvider.ImageContoursResponse;
import edu.ktu.screenshotanalyser.texts.TextExtractor;
import edu.ktu.screenshotanalyser.texts.TextExtractor.TextExtractRequest;
import edu.ktu.screenshotanalyser.texts.TextExtractor.TextExtractResponse;

public class MyTest {

	@Test
	public void test() {
		ImageContoursProvider contoursProvider = new ImageContoursProvider();
		TextExtractor extractor = new TextExtractor(0.65f, "./tessdata", "nor");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String dir = "./samples2";
		String resultsDir = "./results";
		File main = new File(dir);
		File resultsFile = new File(resultsDir);
		File[] apkFiles = main.listFiles(x -> x.getName().endsWith(".apk") && x.isFile());
		System.out.println("FOUND " + apkFiles.length);
		for (File apk : apkFiles) {
			System.out.println("APK: " + apk);
			String projectApkName = apk.getName();
			System.out.println("ShortName: " + projectApkName);
			File[] foundResultsForApk = main.listFiles(x -> x.getName().startsWith(projectApkName) && x.isDirectory());

			for (File foundDir : foundResultsForApk) {
				String expName = foundDir.getName();
				String config = foundDir.getName().replaceAll(projectApkName, "").replaceFirst("_", "");
				System.out.println("config: " + config);
				File[] screens = new File(foundDir, "states").listFiles(x -> x.getName().startsWith("screen_"));

				for (File screen : screens) {
					File analyzedScreen = Paths.get(resultsDir, expName, screen.getName().replace(".png", ".json"))
							.toFile();
					if (!analyzedScreen.exists()) {
						analyzedScreen.getParentFile().mkdirs();
						try {
							// List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT,
							// label);

							ImageContoursResponse contours = contoursProvider
									.getContours(new ImageContoursProviderRequest(screen));
							TextExtractResponse response = extractor
									.extract(new TextExtractRequest(screen, contours.getBounds()));
							FileUtils.write(analyzedScreen, gson.toJson(response));

						} catch (Throwable e) {
							e.printStackTrace();
						}

					}
				}
				System.out.println(screens);

			}
		}
	}

}
