package edu.ktu.screenshotanalyser.reporters;

/*
import edu.ktu.screenshotanalyser.reporters.FoundTextsOnImagesReporter.AppExtractInfo;

public class MultIReporter implements IReporter {
	private final IReporter[] reporters;

	public MultIReporter(String outDir) {
		this.reporters = new IReporter[] { new JsonResultsReporter(outDir), new FoundTextsOnImagesReporter(outDir) };
	}

	@Override
	public void save(AppExtractInfo request) throws Throwable {
		for (IReporter reporter : this.reporters) {
			reporter.save(request);
		}

	}

}
*/