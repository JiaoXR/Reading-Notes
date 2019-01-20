package com.jaxer.example.dom;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * 解析XML文件测试
 * 原文链接：https://www.cnblogs.com/deng-cc/p/6349116.html
 * <p>
 * Created by jaxer on 2019/1/20
 */
public class ParseDOM {

	public static void main(String[] args) {
		// 创建 DocumentBuilderFactory 对象
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			// 创建 DocumentBuilder 对象
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse("src/com/jaxer/example/dom/books.xml");

			// 获取所有 book 节点
			NodeList nodeList = document.getElementsByTagName("book");
			System.out.println("共有 " + nodeList.getLength() + " 本书");

			Element element = document.getDocumentElement();
			System.out.println("element = " + element);

			System.out.println("——————————————————————");

			// 遍历每个 book 节点
			for (int i = 0; i < nodeList.getLength(); i++) {
				// 通过 item(i) 获取 book 节点（从 0 开始）
				Node node = nodeList.item(i);
				// 获取 book 节点的所有属性集合
				NamedNodeMap attributes = node.getAttributes();

				// 遍历节点的每一个属性
				System.out.println("第 " + (i + 1) + " 本书有 " + attributes.getLength() + " 个属性，分别为：");
				for (int j = 0; j < attributes.getLength(); j++) {
					// 获取属性
					Node item = attributes.item(j);
					// 获取属性的名称
					String nodeName = item.getNodeName();
					System.out.println("属性名：" + nodeName);
				}
				System.out.println();

				// 解析 book 的子节点
				System.out.println("子节点为：");
				NodeList childNodes = node.getChildNodes();
				for (int k = 0; k < childNodes.getLength(); k++) {
					// 获取子节点
					Node childNode = childNodes.item(k);
					// 筛选子节点（区分 TEXT 和 ELEMENT 类型的 node）
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						// 获取节点名和内容（两种方法）
						System.out.println(childNode.getNodeName() + " : " + childNode.getTextContent());
//						System.out.println(childNode.getNodeName() + " : " + childNode.getFirstChild().getTextContent());
					}
				}
				System.out.println("——————————————————————");
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
}
