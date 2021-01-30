package aufgabenGenerator;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;

public class XMLObject {
	private String bezeichnung=""; //Tagbezeichnung
	private HashMap<String,String> attribute = new HashMap<String,String>();
	private String content = "";
	private ArrayList<XMLObject> children = new ArrayList<XMLObject>();
	private int currentChild = 0;
	
	public XMLObject(Document doc) {
		
	}
	
	public XMLObject(String bezeichnung) {
		this(bezeichnung,"");
	}
	
	public XMLObject(String bezeichnung, String content) {
		this(bezeichnung,content,null);
	}

	/**
	 * @param bezeichnung
	 * @param content
	 * @param attribute
	 */
	@SuppressWarnings("unchecked")
	public XMLObject(String bezeichnung, String content, HashMap<String, String> attribute) {
		//TODO: Überprüfen, dass bezeichnung nur aus buchstaben besteht
		this.bezeichnung = bezeichnung;
		this.content = content;
		if (attribute!=null) this.attribute = (HashMap<String,String>)attribute.clone();
	}
	
	public void addAttribute(String key, String value) {
		//TODO: Eingaben prüfen
		attribute.put(key, value);		
	}
	
	public void addChild(XMLObject c) {
		children.add(c);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}
	
	public int getChildcount() {
		return children.size();
	}
	
	/**
	 * Returns the Value of Attribute type or null if not exists
	 * @param type
	 * @return value of Attribute type
	 */
	public String getAttribute(String type) {
		return attribute.get(type);
	}
	
	public XMLObject getChild(int index) {
		if (index < children.size()) {
			return children.get(index);
		}
		return null;
	}
	
	
	
	public XMLObject getChild(String type) {
		for (XMLObject obj: children) {
			if (obj.getBezeichnung().equals(type)) return obj;
		}
		return null;
	}

	@Override
	public String toString() {
		String out="";
		// Start-Tag erzeugen
		out +="<"+bezeichnung;
		for (String key: attribute.keySet()) {
			out+=" "+key+"=\""+attribute.get(key)+"\"";
		}
		out +=">";
		// Children oder content
		if (children.isEmpty()) { // Nur Content einfügen
			out+=this.content;
		} else { // Children einfügen
			out+="\n";
			for (XMLObject x : children) {
				out+=x.toString().replaceAll("^", "  ").replaceAll("\n", "\n  ").replaceAll("  $", "");
			}
		}
		
		//End-Tag erzeugen
		out +="</"+bezeichnung+">\n";		
		return out;
	}
	
	@SuppressWarnings("unchecked")
	public XMLObject clone() {
		XMLObject n = new XMLObject(bezeichnung, content, (HashMap<String,String>)attribute.clone());
		for (XMLObject c: children) {
			n.addChild(c.clone());
		}
		return n;		
	}

	public void toFirstChild() {
		this.currentChild=0;
	}

	public boolean hasChildAccess() {
		return this.currentChild < children.size();
	}

	public void toNextChild() {
		this.currentChild++;		
	}
	
	public XMLObject getCurrentChild() {
		if (this.hasChildAccess()) return children.get(currentChild);
		return null;
	}
}
