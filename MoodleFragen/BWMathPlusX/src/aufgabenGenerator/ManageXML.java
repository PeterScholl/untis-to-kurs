package aufgabenGenerator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.io.StringReader;

public class ManageXML {

	public static Document parseString(String input) {

		try {

			// File fXmlFile = new File("/Users/mkyong/staff.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			// Document doc = dBuilder.parse(fXmlFile);
			InputSource is = new InputSource(new StringReader(input));
			Document doc = dBuilder.parse(is);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			//System.out.println(documentToXML(doc).toString());
			return doc;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public static XMLObject documentToXML(Document doc) {
		doc.getDocumentElement().normalize();

		Node root = doc.getDocumentElement();
		// root.getChildNodes();
		// NamedNodeMap attr = root.getAttributes();
		return nodeToXML(root);
	}

	public static XMLObject nodeToXML(Node n) {
		// System.out.println("Root element :"+n.getNodeName());
		XMLObject x = new XMLObject(n.getNodeName());
		// Attribute einfügen
		NamedNodeMap attr = n.getAttributes();
		for (int i = 0; i < attr.getLength(); i++) {
			Node nAttr = attr.item(i);
			// System.out.println("Attribut: "+nAttr.getNodeName()+" =
			// "+nAttr.getNodeValue());
			x.addAttribute(nAttr.getNodeName(), nAttr.getNodeValue());
		}
		NodeList children = n.getChildNodes();
		if (children.getLength() == 1) {
			//System.out.println("Node: "+children.item(0).getNodeName()+" - type:"+children.item(0).getNodeType()+","+children.item(0).getNodeValue());			
		}
		if (children.getLength() == 1 && children.item(0).getNodeName().equals("#cdata-section")) {
			x.setContent("<![CDATA["+children.item(0).getNodeValue()+"]]>");
		} else if (children.getLength() == 1 && children.item(0).getNodeName().equals("#text")) {
			x.setContent(children.item(0).getNodeValue());
		} else {
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
					x.addChild(nodeToXML(children.item(i)));
				}
			}
		}
		return x;
	}

}
