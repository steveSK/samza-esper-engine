/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.parser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import kafka.javaapi.producer.Producer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import samzaesper.statement.EsperStatement;

/**
 *
 * @author stefan
 */
public class StatementConfigurationParser {

    public static List<EsperStatement> parseStatements(URL url, Producer producer,String outputTopic, String eventNameKey) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(url.openStream());
        NodeList statementsListXML = doc.getElementsByTagName("statement");
        List<EsperStatement> statements = new ArrayList<>();
        for (int i = 0; i < statementsListXML.getLength(); i++) {
            Element el = (Element) statementsListXML.item(i);
            String name = el.getAttribute("name");
            String definition = el.getAttribute("definition");
            boolean isPattern = Boolean.valueOf(el.getAttribute("is-pattern"));
        //    String outputTopic = el.getAttribute("output-topic");
            String partitionKey = el.getAttribute("partition-key");
            statements.add(new EsperStatement(name, definition, isPattern, producer, outputTopic, partitionKey, eventNameKey));
        }
        return statements;
    }
}
