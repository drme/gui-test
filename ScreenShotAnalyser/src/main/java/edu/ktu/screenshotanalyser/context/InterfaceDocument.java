package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import edu.ktu.screenshotanalyser.utils.LazyObject;

abstract class InterfaceDocument
{
	InterfaceDocument(File file)
	{
		this.file = file;
	}
	
	public UIElement getRoot()
	{
		return this.rootElement.instance();
	}
	
	synchronized List<UIElement> findElement(String id)
	{
		var root = this.rootElement.instance();
		
		if (null != root)
		{
			return root.findElement(id);
		}
		else
		{
			return Collections.emptyList();
		}
	}
	
	private UIElement parseLayout()
	{
		try
		{
			var factory = DocumentBuilderFactory.newInstance();
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			
			var builder = factory.newDocumentBuilder();
			var document = builder.parse(this.file);

			document.getDocumentElement().normalize();

			var root = document.getDocumentElement();
			var result = new UIElement(root, null);

			addChildElements(result, root);

			return result;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return null;
		}
	}
	
	private static void addChildElements(UIElement parentUIElement, Element parent)
	{
		var nodes = parent.getChildNodes();

		for (var count = 0; count < nodes.getLength(); count++)
		{
			var elemNode = nodes.item(count);

			if (elemNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
			{
				var childElement = new UIElement((Element) elemNode, parentUIElement);

				parentUIElement.addChild(childElement);

				addChildElements(childElement, (Element) elemNode);
			}
		}
	}
	
	protected final File file;
	private final LazyObject<UIElement> rootElement = new LazyObject<>(() -> parseLayout());
}
