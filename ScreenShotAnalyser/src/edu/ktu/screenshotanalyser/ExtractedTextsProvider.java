package edu.ktu.screenshotanalyser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import edu.ktu.screenshotanalyser.reporters.FoundTextsOnImagesReporter.AppExtractInfo;

public class ExtractedTextsProvider implements IFilesProvider<AppExtractInfo> {
	private final Gson gson = new Gson();

	public AppExtractInfo[] getFiles(String baseDir) {
		final List<AppExtractInfo> items = new ArrayList<AppExtractInfo>();
		for (File f : new File(baseDir).listFiles()) {
			if (f.getName().endsWith(".json")) {
				try {
					items.add(gson.fromJson(FileUtils.readFileToString(f), AppExtractInfo.class));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}
		return items.toArray(new AppExtractInfo[0]);
	}

}
