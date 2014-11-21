package cn.freeteam.cms.util;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * <p>Title: DOMUtil.java</p>
 * 
 * <p>Description: xml解析工具类</p>
 * 
 * <p>Date: May 17, 2012</p>
 * 
 * <p>Time: 12:48:04 PM</p>
 * 
 * <p>Copyright: 2012</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @author freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class DOMUtil {

	/**
	 * 获取指定文件里所有指定元素
	 * @param filepath
	 */
	public static NodeList findElements(String filepath,String tagname){
		try{
		    // 获取一个XML文件的解析器.
		    DocumentBuilderFactory factory = 
		     DocumentBuilderFactory.newInstance();
		    // 解析XML文件生成DOM文档的接口类，以便访问DOM.
		    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
		    factory.setIgnoringElementContentWhitespace(true); //eliminatewhite spaces
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    // Document接口描述对应XML文件的文档树.
		    Document document = builder.parse(new File(filepath));
		    // 获取根元素的子节点列表.
		    return document.getElementsByTagName(tagname);
		   } catch(Exception e){
		    e.printStackTrace();
		   }

		return null;
	}
	/**
	 * 获取属性值
	 * @param node
	 * @param attrName
	 * @return
	 */
	public static String getAttr(Node node,String attrName){
		NamedNodeMap namedNodeMap = node.getAttributes();
		if (namedNodeMap!=null ) {
			Node attr=namedNodeMap.getNamedItem(attrName);
			if (attr!=null) {
				return attr.getNodeValue();
			}
		}
		return null;
	}
}
