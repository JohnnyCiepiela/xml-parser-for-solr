import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StreamingXMLParser {
    public static void main(String[] args) {
        String inputXmlFile = "drugs.xml";
        String outputXmlFile = "output.xml";

        try {
            List<DrugItem> drugItems = parseXML(inputXmlFile);
            generateOutputXML(drugItems, outputXmlFile);
            System.out.println("Output written to: " + outputXmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<DrugItem> parseXML(String xmlFile) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DrugHandler handler = new DrugHandler();
        saxParser.parse(xmlFile, handler);
        return handler.getDrugItems();
    }

    private static void generateOutputXML(List<DrugItem> drugItems, String outputXmlFile) throws Exception {
        StringBuilder outputXml = new StringBuilder("<add>\n");
        for (DrugItem item : drugItems) {
            outputXml.append("   <doc>\n");
            outputXml.append("      <field name=\"DrugId\">").append(item.getDrugId()).append("</field>\n");
            outputXml.append("      <field name=\"DrugFullName\">").append(item.getDrugFullName()).append("</field>\n");
            outputXml.append("      <field name=\"DrugShortName\">").append(item.getDrugShortName()).append("</field>\n");
            outputXml.append("      <field name=\"DrugDosage\"></field>\n");
            outputXml.append("      <field name=\"DrugForm\">").append(item.getDrugForm()).append("</field>\n");
            outputXml.append("      <field name=\"DrugPackage\">").append(item.getDrugPackage()).append("</field>\n");
            outputXml.append("      <field name=\"Bloz7\">").append(item.getBloz7()).append("</field>\n");
            outputXml.append("   </doc>\n");
        }
        outputXml.append("</add>");

        Path outputPath = Path.of(outputXmlFile);
        Files.write(outputPath, outputXml.toString().getBytes(), StandardOpenOption.CREATE);
    }

    private static class DrugHandler extends DefaultHandler {
        private List<DrugItem> drugItems = new ArrayList<>();
        private DrugItem currentDrugItem;
        private StringBuilder currentText;

        public List<DrugItem> getDrugItems() {
            return drugItems;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            currentText = new StringBuilder();
            if ("item".equals(qName)) {
                currentDrugItem = new DrugItem();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            currentText.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("item".equals(qName)) {
                drugItems.add(currentDrugItem);
            } else if ("DrugId".equals(qName)) {
                currentDrugItem.setDrugId(Integer.parseInt(currentText.toString().trim()));
            } else if ("DrugFullName".equals(qName)) {
                currentDrugItem.setDrugFullName(currentText.toString().trim());
            } else if ("DrugShortName".equals(qName)) {
                currentDrugItem.setDrugShortName(currentText.toString().trim());
            } else if ("DrugForm".equals(qName)) {
                currentDrugItem.setDrugForm(currentText.toString().trim());
            } else if ("DrugPackage".equals(qName)) {
                currentDrugItem.setDrugPackage(currentText.toString().trim());
            } else if ("Bloz7".equals(qName)) {
                currentDrugItem.setBloz7(Integer.parseInt(currentText.toString().trim()));
            }
        }
    }
}