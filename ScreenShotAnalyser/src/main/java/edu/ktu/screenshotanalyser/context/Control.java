package edu.ktu.screenshotanalyser.context;

import org.opencv.core.Rect;

/**
 * Recognized user interface control from image.
 */
public class Control
{
	public Control(State state, String text, String contentDescription, Rect bounds, Integer parentId, Integer id, String type, boolean visible, String signature)
	{
		this.state = state;
		this.text = text;
		this.contentDescription = contentDescription;
		this.bounds = bounds;
		this.parentId = parentId;
		this.id = id;
		this.type = type;
		this.visible = visible;
		this.signature = signature;
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
	
	public String getSignature()
	{
		return this.signature;
	}
	
	public boolean isOverlapping(Control other)
	{
		int d = 2;
		
    if (this.bounds.y + d >= other.bounds.y + other.bounds.height || this.bounds.y + this.bounds.height - d - d <= other.bounds.y)
    {
    	return false;
    }
    
    if (this.bounds.x + this.bounds.width - d -d <= other.bounds.x || this.bounds.x + d >= other.bounds.x + other.bounds.width)
    {
    	return false;
    }
     
    return true;		
	}

	protected final Rect bounds;
	protected final String contentDescription;
	protected Control parent = null;
	protected final String text;
	protected final Integer parentId;
	protected final Integer id;
	protected final String type;
	protected final boolean visible;
	protected final State state;
	protected final String signature;
}
