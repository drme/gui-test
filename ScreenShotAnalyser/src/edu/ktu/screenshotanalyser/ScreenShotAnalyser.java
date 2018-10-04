package edu.ktu.screenshotanalyser;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sampullara.cli.Args;
import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.checks.MultiChecker;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.DefaultContextProvider;
import edu.ktu.screenshotanalyser.reporters.FoundTextsOnImagesReporter.AppExtractInfo;

public class ScreenShotAnalyser
{

	private static void extractTextsFromImages(AnalyzerSettings request, String inputDir, String textsOutDir)
	{
		System.load(request.getCvLibrary());
		ImageTextsExtractor extractor = ImageTextsExtractor.fromRequest(request);
		extractor.extract(new AnalyzerRequest(inputDir, textsOutDir));
	}

	static DefaultContextProvider provider = new DefaultContextProvider();
	static ExtractedTextsProvider textsProvider = new ExtractedTextsProvider();
	static ICheck appContextCheck = MultiChecker.getAppContextChecks();
	static ICheck imageBasedChecks = MultiChecker.getImageBasedChecks();
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static void runChecks(String projectName, File resourcesDir, File[] imagesDir, String outDir) throws IOException
	{
		AppContext context = provider.getContext(resourcesDir.getAbsolutePath());
		List<CheckResult> results = new ArrayList<CheckResult>();
		results.addAll(Arrays.asList(appContextCheck.analyze(new CheckRequest("-"), context)));
		for (File dir : imagesDir)
		{
			AppExtractInfo[] infos = textsProvider.getFiles(dir.getAbsolutePath());
			for (AppExtractInfo info : infos)
			{
				CheckRequest request = new CheckRequest(info.getShortFileName());
				request.getExtractedTexts().addAll(info.getExtractedTexts());
				results.addAll(Arrays.asList(imageBasedChecks.analyze(request, context)));
			}
		}
		if (!results.isEmpty())
		{
			FileUtils.write(new File(outDir, "violations.json"), gson.toJson(results));

		}

	}

	public static void main(String[] args) throws Throwable
	{
		
		 try (Graph g = new Graph()) {
	      final String value = "Hello from " + TensorFlow.version();

	      // Construct the computation graph with a single operation, a constant
	      // named "MyConst" with a value "value".
	      try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
	        // The Java API doesn't yet include convenience functions for adding operations.
	        g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
	      }

	      // Execute the "MyConst" operation in a Session.
	      try (Session s = new Session(g);
	          // Generally, there may be multiple output tensors,
	          // all of them must be closed to prevent resource leaks.
	          Tensor output = s.runner().fetch("MyConst").run().get(0)) {
	        System.out.println(new String(output.bytesValue(), "UTF-8"));
	      }
	    }

		
		
		
		/*

		//String baseDir = "D:/darbai/screenshots";
		String baseDir = "e:/2/";
		String textsDir = baseDir + "/texts";
		String droidbotImagesDir = baseDir + "/images";
		String resourcesDir = baseDir + "/resources";

		String resultsOutDir = "./out/results";

		AnalyzerSettings request = new AnalyzerSettings();
		List<String> unparsed = Args.parseOrExit(request, new String[0]);
		request.setImagesDir(droidbotImagesDir);
		request.setOutputDir(resultsOutDir);
		request.setResourcesDir(resourcesDir);
		request.setTextsDir(textsDir);

		DirectoriesProvider provider = new DirectoriesProvider();
		File[] files = provider.getFiles(request.getResourcesDir());

		for (File resourceDir : files)
		{

			final String projectname = resourceDir.getName();
			System.out.println(projectname);
			File[] extractedTextsDirectories = new File(textsDir).listFiles(new FileFilter()
			{

				@Override
				public boolean accept(File pathname)
				{
					return pathname.getName().startsWith(projectname);
				}
			});
			
			runChecks(projectname, resourceDir, extractedTextsDirectories, new File(resultsOutDir, projectname).getAbsolutePath());
		} */
	};

};
