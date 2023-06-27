package edu.ktu.screenshotanalyser.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;

public class UIElement
{
	UIElement(Element element, UIElement parent)
	{
		this.id = getAttribute(element, "id");
		this.textColor = new Color(getAttribute(element, "textColor"));
		this.background = getAttribute(element, "background");
		this.parent = parent;
		this.type = element.getTagName();
		this.drawable = getAttribute(element, "drawable");

		var src1 = getAttribute(element, "src");
		var srcCompat = getAttribute(element, "srcCompat");

		this.src = src1 != null ? src1 : srcCompat;
		
		this.layoutHeight = getAttribute(element, "layout_height");
		this.layoutWidth = getAttribute(element, "layout_width");
	}
	
	private static String getAttribute(Element element, String attributeName)
	{
		var attributes = element.getAttributes();
		
		for (int i = 0; i < attributes.getLength(); i++)
		{
			var attribute = attributes.item(i);
			
			var name = attribute.getNodeName();
			
			if (name.contains(":"))
			{
				name = name.split(":")[1];
			}
			
			if (attributeName.equals(name))
			{
				return attribute.getNodeValue();
			}
		}
		
		return null;
	}
	
	List<UIElement> findElement(String id)
	{
		if (id.equals(this.id))
		{
			return Collections.singletonList(this);
		}

		var result = new ArrayList<UIElement>();
		
		for (var element : this.children)
		{
			result.addAll(element.findElement(id));
		}
		
		return result;
	}

	void addChild(UIElement element)
	{
		this.children.add(element);
	}

	public Color getTextColor()
	{
		return this.textColor;
	}

	public String getBackground()
	{
		return this.background;
	}
	
	public UIElement getParent()
	{
		return this.parent;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public String getSrc()
	{
		return this.src;
	}

	public String getDrawable()
	{
		return this.drawable;
	}
	
	public boolean hasImage()
	{
		if (this.background != null && (this.background.startsWith("@drawable/") || this.background.startsWith("@mipmap/")))
		{
			return true;
		}
		
		return (this.src != null && (this.src.startsWith("@drawable/") || this.src.startsWith("@mipmap/")));
	}
	
	public List<UIElement> getChildren()
	{
		return this.children;
	}

	public String getLayoutWidth()
	{
		return this.layoutWidth;
	}

	public String getLayoutHeight()
	{
		return this.layoutHeight;
	}
	
	private final String id;
	private final Color textColor;
	private List<UIElement> children = new ArrayList<>();
	private final String background;
	private final UIElement parent;
	private final String type;
	private final String src;
	private final String drawable;
	private final String layoutWidth;
	private final String layoutHeight;
}
