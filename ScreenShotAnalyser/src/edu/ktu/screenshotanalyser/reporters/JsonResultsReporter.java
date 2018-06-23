package edu.ktu.screenshotanalyser.reporters;

import java.io.File;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.ktu.screenshotanalyser.reporters.FoundTextsOnImagesReporter.AppExtractInfo;

public class JsonResultsReporter implements IReporter {

	private String outDir;

	public JsonResultsReporter(String outDir) {
		this.outDir = outDir;
	}

	private final Gson serializer = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public void save(AppExtractInfo request) throws Throwable {
		String jsonString = serializer.toJson(request);
		File outFile = new File(outDir, request.getOutFile() + ".json");
		outFile.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(outFile);
		writer.write(jsonString);
		writer.flush();
		writer.close();

	}

}
