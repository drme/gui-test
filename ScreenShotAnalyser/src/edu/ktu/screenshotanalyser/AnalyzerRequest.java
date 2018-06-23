package edu.ktu.screenshotanalyser;

public class AnalyzerRequest {

	@Override
	public String toString() {
		return "AnalyzerRequest [outDir=" + outDir + ", baseDir=" + baseDir + "]";
	}

	public String getOutDir() {
		return outDir;
	}

	public String getBaseDir() {
		return baseDir;
	}

	private String outDir;
	private String baseDir;

	public AnalyzerRequest(String baseDir, String outDir) {
		this.baseDir = baseDir;
		this.outDir = outDir;

	}
}
