package edu.ktu.screenshotanalyser;

import java.io.File;

public interface IFilesProvider {
	AppImageFiles[] getFiles(String baseDir);
}
