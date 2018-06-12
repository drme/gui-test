package edu.ktu.screenshotanalyser;

import java.io.File;

public interface IFilesProvider {
	File[] getFiles(String baseDir);
}
