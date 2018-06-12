package edu.ktu.screenshotanalyser;

public class ScreenShotAnalyser {

	public static void main(String[] args) throws Throwable {

		String baseDir = System.getProperty("user.dir") + "\\samples\\";
		
		if (args.length >= 1) {
			baseDir = args[0];
		}
		
		App app = new App();
		app.work(baseDir);

	};

};
