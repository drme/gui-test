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
	protected String type;
	protected boolean visible;

	public Control(String text, String contentDescription, Rect bounds, Integer parentId, Integer id, String type, boolean visible)
	{
		this.text = text;
		this.contentDescription = contentDescription;
		this.bounds = bounds;
		this.parentId = parentId;
		this.id = id;
		this.type = type;
		this.visible = visible;
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
	
	public String getType()
	{
		return this.type;
	}
	
	public boolean isVisible()
	{
		return this.visible;
	}
}
