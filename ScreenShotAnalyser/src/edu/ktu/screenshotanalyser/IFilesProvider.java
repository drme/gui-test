package edu.ktu.screenshotanalyser;

import java.io.File;

public interface IFilesProvider<T> {
	T[] getFiles(String baseDir);
}
