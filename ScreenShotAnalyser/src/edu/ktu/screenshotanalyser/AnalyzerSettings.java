package edu.ktu.screenshotanalyser;

import java.io.File;

import com.sampullara.cli.Argument;

public class AnalyzerSettings {
	public String getInputDir() {
		return inputDir;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public String getCvLibrary() {
		return new File(cvLibrary).getAbsolutePath();
	}
	
	public String getTessDataFolder() {
		return tessDataFolder;
	}

	public float getPrecision() {
		return precision;
	}

	public boolean isRunChecks() {
		return runChecks;
	}

	@Argument(alias = "i", description = "Input directory", required = false)
	private String inputDir = ".\\samples\\images";
	
	@Argument(alias = "o", description = "Output directory", required = false)
	private String outputDir =  ".\\out\\";
	
	@Argument(alias = "cv", description = "Path to opencv dll file", required = false)
	private String cvLibrary = ".\\lib\\opencv_java340.dll";

	@Argument(alias = "td", description = "Path to tessdata", required = false )
	private String tessDataFolder = ".\\tessdata";


	@Argument(alias = "p", description = "Precision for words detection", required = false)
	private float precision = 0.60f;

	@Argument(alias = "lang", description = "Language", required = false)
	private String language = "nor";
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setInputDir(String inputDir) {
		this.inputDir = inputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public void setCvLibrary(String cvLibrary) {
		this.cvLibrary = cvLibrary;
	}

	public void setTessDataFolder(String tessDataFolder) {
		this.tessDataFolder = tessDataFolder;
	}

	public void setPrecision(float precision) {
		this.precision = precision;
	}

	public void setRunChecks(boolean runChecks) {
		this.runChecks = runChecks;
	}

	@Override
	public String toString() {
		return "AnalyzerRequest [inputDir=" + inputDir + ", outputDir=" + outputDir + ", cvLibrary=" + cvLibrary
				+ ", tessDataFolder=" + tessDataFolder + ", precision=" + precision + ", runChecks=" + runChecks + "]";
	}

	@Argument(alias = "c", description = "Flag if to run checks", required = false)
	private boolean runChecks = false;
	
}
