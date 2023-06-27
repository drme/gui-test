package edu.ktu.screenshotanalyser.context;

import java.util.List;
import org.opencv.core.Rect;

/**
 * Recognized user interface control from image.
 */
public class Control
{
	public Control(State state, String text, String contentDescription, Rect bounds, Integer parentId, Integer id, String type, boolean visible, String signature, String resourceId, String packageName, boolean clickable, List<Integer> childrenIds)
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
		this.resourceId = resourceId;
		this.packageName = packageName;
		this.clickable = clickable;
		this.childrenIds = childrenIds;
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

	void setLinks(Control control, List<Control> children)
	{
		this.parent = control;
		this.children = children;
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
	
	public String getResourceId()
	{
		return this.resourceId;
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
	
	public synchronized boolean isAd()
	{
		if (null == this.isAdd)
		{
			this.isAdd = isAd(this);
		}
		
		return this.isAdd.booleanValue();
	}
	
	private static boolean isAd(Control control)
	{
		if (null == control)
		{
			return false;
		}
		
		if ("Test Ad".equals(control.getText()))
		{
			return true;
		}
		
		if (control.getSignature().contains("addview"))
		{
			return true;
		}
		else
		{
			return isAd(control.getParent());
		}
	}	

	public boolean isOffScreen()
	{
		if ((this.bounds.width <= 1) || (this.bounds.height <= 1))
		{
			return true;
		}

		if ((this.bounds.x >= this.state.getImageSize().width) || (this.bounds.y >= this.state.getImageSize().height))
		{
			return true;
		}

		return ((this.bounds.x + this.bounds.width <= 1) || (this.bounds.y + this.bounds.height <= 1));		
	}
	
	public boolean isAtTheScreenEdge(Integer navigationBarY)
	{
		var height = navigationBarY != null ? navigationBarY : this.state.getImageSize().height; 
		
		if (this.bounds.x + this.bounds.width == this.state.getImageSize().width)
		{
			return (float)this.bounds.width / (float)this.state.getImageSize().width < 0.02;
		}
		
		if (this.bounds.y + this.bounds.height == height)
		{
			return (float)this.bounds.height / (float)this.state.getImageSize().height < 0.02;
		}
		
		return false;
	}
	
	public String getPackageName()
	{
		return this.packageName;
	}
	
	public boolean isClickable()
	{
		return this.clickable;
	}
	
	public List<Integer> getChildrenIds()
	{
		return this.childrenIds;
	}

	public List<Control> getChildren()
	{
		return this.children;
	}
	
	public String toString()
	{
		return this.type + " " + this.bounds.toString();
	}
	
	private final Rect bounds;
	private final String contentDescription;
	private Control parent = null;
	private final String text;
	private final Integer parentId;
	private final Integer id;
	private final String type;
	private final boolean visible;
	private final State state;
	private final String signature;
	private final String resourceId;
	private Boolean isAdd = null;
	private final String packageName;
	private final boolean clickable;
	private final List<Integer> childrenIds;
	private List<Control> children = null;
	public static final String LAUNCHER_PACKAGE = "com.google.android.apps.nexuslauncher";
}
