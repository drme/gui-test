package edu.ktu.screenshotanalyser.context;

import org.opencv.core.Rect;

/**
 * Recognized user interface control from image.
 */
public class Control
{
	protected Rect bounds;
	protected String contentDescription;
	protected Control parent;
	protected String text;
	protected Integer parentId;
	protected Integer id;

	public Control(String text, String contentDescription, Rect bounds, Integer parentId, Integer id)
	{
		this.text = text;
		this.contentDescription = contentDescription;
		this.bounds = bounds;
		this.parentId = parentId;
		this.id = id;
	}
	
	public String getContentDescription()
	{
		return this.contentDescription;
	}

	public Rect getBounds()
	{
		return this.bounds;
	}
	
	public Control getParent()
	{
		return this.parent;
	}
	
	public long getId()
	{
		return this.id;
	}
	
	public long getParentId()
	{
		return this.parentId;
	}

	public void setParent(Control control)
	{
		this.parent = control;
	}
	
	public String getText()
	{
		return this.text;
	}
}
