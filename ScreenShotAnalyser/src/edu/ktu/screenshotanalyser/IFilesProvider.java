package edu.ktu.screenshotanalyser;

public interface IFilesProvider<T>
{
	T[] getFiles(String baseDir);
}
