package aufgabenGenerator;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;

public class XMLObject {
	private String bezeichnung = ""; // Tagbezeichnung
	private HashMap<String, String> attribute = new HashMap<String, String>();
	private String content = "";
	private ArrayList<XMLObject> children = new ArrayList<XMLObject>();
	private int currentChild = 0;

	public XMLObject(Document doc) {

	}

	public XMLObject(String bezeichnung) {
		this(bezeichnung, "");
	}

	public XMLObject(String bezeichnung, String content) {
		this(bezeichnung, content, null);
	}

	/**
	 * @param bezeichnung
	 * @param content
	 * @param attribute
	 */
	@SuppressWarnings("unchecked")
	public XMLObject(String bezeichnung, String content, HashMap<String, String> attribute) {
		// TODO: Überprüfen, dass bezeichnung nur aus buchstaben besteht
		this.bezeichnung = bezeichnung;
		this.content = content;
		if (attribute != null)
			this.attribute = (HashMap<String, String>) attribute.clone();
	}

	public void addAttribute(String key, String value) {
		// TODO: Eingaben prüfen
		attribute.put(key, value);
	}

	public void addAttribute(String[] types, String value) {
		if (types == null || types.length == 0)
			return;
		String[] childlist = new String[types.length - 1];
		for (int i = 0; i < childlist.length; i++)
			childlist[i] = types[i];
		XMLObject child = this.getChild(childlist);
		if (child != null)
			child.addAttribute(types[types.length - 1], value);
	}

	public void addChild(XMLObject c) {
		children.add(c);
	}

	/**
	 * Gibt den Inhalt dieses XML-Elements zurück
	 * @return den Content zwischen den Tags <bez></bez>
	 */
	public String getContent() {
		return content;
	}

	/**
	 * gibt den Inhalt des durch types festgelegte Kind-Folge-Objekt aus - sonst null
	 * @param types Kindfolge
	 * @return content des beschriebenen Elements oder null
	 */
	public String getContent(String[] types) {
		if (types == null)
			return null;
		if (types.length == 0)
			return this.getContent();
		XMLObject child = this.getChild(types[0]);
		if (child == null)
			return null;
		String[] ntypes = new String[types.length - 1];
		for (int i = 0; i < ntypes.length; i++)
			ntypes[i] = types[i + 1];
		return child.getContent(ntypes);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setContent(String[] types) {
		if (types == null || types.length == 0)
			return;
		if (types.length == 1)
			this.setContent(types[0]);
		else {
			XMLObject child = this.getChild(types[0]);
			if (child == null) {
				child = new XMLObject(types[0]);
				this.addChild(child);
			}
			String[] ntypes = new String[types.length - 1];
			for (int i = 0; i < ntypes.length; i++)
				ntypes[i] = types[i + 1];
			child.setContent(ntypes);
		}
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public int getChildcount() {
		return children.size();
	}

	/**
	 * Returns the Value of Attribute type or null if not exists
	 * 
	 * @param type
	 * @return value of Attribute type
	 */
	public String getAttribute(String type) {
		return attribute.get(type);
	}

	public String getAttribute(String[] types) {
		if (types == null || types.length == 0)
			return null;
		String[] childlist = new String[types.length - 1];
		for (int i = 0; i < childlist.length; i++)
			childlist[i] = types[i];
		XMLObject child = this.getChild(childlist);
		if (child != null)
			return child.getAttribute(types[types.length - 1]);
		return "";
	}

	public XMLObject getChild(int index) {
		if (index < children.size()) {
			return children.get(index);
		}
		return null;
	}

	/**
	 * Gibt das erste Kind vom Typ type zurück, sonst null
	 * @param type der gesuchte Typ/Bezeichnung
	 * @return das Kind von diesem Typ
	 */
	public XMLObject getChild(String type) {
		for (XMLObject obj : children) {
			if (obj.getBezeichnung().equals(type))
				return obj;
		}
		return null;
	}

	/**
	 * folgt dem XML-Baum entlang des String[] types und gibt das entsprechende XML-Objekt am Ende des Pfades zurück
	 * @param types beschreibt die Typen der Objekte denen zu folgen ist
	 * @return das Objekt am Ende des Pfades
	 */
	public XMLObject getChild(String[] types) {
		if (types == null)
			return null;
		if (types.length == 0)
			return this;
		XMLObject child = this.getChild(types[0]);
		if (child == null)
			return null;
		String[] ntypes = new String[types.length - 1];
		for (int i = 0; i < ntypes.length; i++)
			ntypes[i] = types[i + 1];
		return child.getChild(ntypes);
	}
	
	/**
	 * gibt einer Lister aller Kinder des aktuellen Objekts vom angegeben Typ zurück
	 * @param type
	 * @return ArrayList dieser Kind-Objekte
	 */
	public ArrayList<XMLObject> getAllChildren(String type) {
		ArrayList<XMLObject> retChildren = new ArrayList<XMLObject>();
		for (XMLObject x: children) {
			if (x.getBezeichnung().equals(type)) retChildren.add(x);
		}
		return retChildren;
	}

	@Override
	public String toString() {
		String out = "";
		// Start-Tag erzeugen
		out += "<" + bezeichnung;
		for (String key : attribute.keySet()) {
			out += " " + key + "=\"" + attribute.get(key) + "\"";
		}
		out += ">";
		// Children oder content
		if (children.isEmpty()) { // Nur Content einfügen
			out += this.content;
		} else { // Children einfügen
			out += "\n";
			for (XMLObject x : children) {
				out += x.toString().replaceAll("^", "  ").replaceAll("\n", "\n  ").replaceAll("  $", "");
			}
		}

		// End-Tag erzeugen
		out += "</" + bezeichnung + ">\n";
		return out;
	}

	@SuppressWarnings("unchecked")
	public XMLObject clone() {
		XMLObject n = new XMLObject(bezeichnung, content, (HashMap<String, String>) attribute.clone());
		for (XMLObject c : children) {
			n.addChild(c.clone());
		}
		return n;
	}

	public void toFirstChild() {
		this.currentChild = 0;
	}

	public boolean hasChildAccess() {
		return this.currentChild < children.size();
	}

	public void toNextChild() {
		this.currentChild++;
	}

	public XMLObject getCurrentChild() {
		if (this.hasChildAccess())
			return children.get(currentChild);
		return null;
	}

	/**
	 * removes every Child of type
	 * 
	 * @param type
	 */
	public void removeChild(String type) {
		for (int i = children.size() - 1; i >= 0; i--) {
			if (children.get(i).getBezeichnung().equals(type))
				children.remove(i);
		}
	}
}
