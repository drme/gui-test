package edu.ktu.screenshotanalyser.database;

public enum FileType
{
	APPLICATION_PACKAGE(0),
	STORE_ICON(1),
	ADDITIONAL_CONTENT(2);
	
	private final long id;
	
	FileType(long id)
	{
		this.id = id;
  }

	public long id()
	{
		return this.id;
	}
}
