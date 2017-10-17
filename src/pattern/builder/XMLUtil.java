package pattern.builder;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {
	public static Object getBean() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File("resource/config.xml"));

			NodeList nl = doc.getElementsByTagName("className");
			Node n = nl.item(0).getFirstChild();
			String className = n.getNodeValue();

			Object c = Class.forName(className).newInstance();
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
