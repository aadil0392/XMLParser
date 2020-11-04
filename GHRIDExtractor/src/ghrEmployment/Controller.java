package ghrEmployment;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Component;
import model.PanelFormContainer;
import model.PanelLabelMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    @FXML
    public Button submit;
    HTMLWriterUtil fileUtil = new HTMLWriterUtil();
    public List<Component> componentList = new ArrayList<Component>();
    public List<PanelLabelMessage> panelLabelMessageList = new ArrayList<PanelLabelMessage>();
    public List<PanelFormContainer> panelFormContainerList = new ArrayList<PanelFormContainer>();
    String insertFileLoc = "";
    public Map<String, String> partialTriggerToLabel = new HashMap<String, String>();

    @FXML
    public Hyperlink fileLink;

    @FXML
    void selectFileFromLocal(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            String file = selectedFile.getAbsolutePath();
            parseJSFF(file);
            if(file!=null)
                fileLink.setDisable(false);
        }
    }

    @FXML
    void initialize() {

    }

    @FXML
    void generateHTMLReport(MouseEvent event) throws IOException, URISyntaxException, SQLException {
        fileUtil.fileWrite(componentList, panelFormContainerList, panelLabelMessageList);
        fileLink.setVisible(true);

        try {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(String.valueOf(Paths.get(insertFileLoc, "Extractor_"+ java.util.Calendar.getInstance().getTime()+".html"))));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void parseJSFF(String path)
    {
        try {

            File fXmlFile = new File(path);
            //textArea.getText();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList list = doc.getChildNodes();
            for(int i=0;i<list.getLength();i++)
                System.out.println(list.item(i));
            NodeList nList = doc.getElementsByTagName("af:panelGroupLayout");

            System.out.println("----------------------------");
            System.out.println(nList.getLength());
            NodeList panelFlList=doc.getElementsByTagName("af:panelFormLayout");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    System.out.println("PanelGroupLayout id : " + eElement.getAttribute("id"));
                    System.out.println("Partial Trigger : " + eElement.getAttribute("partialTriggers"));
                    panelFormContainerList.add(new PanelFormContainer(eElement.getAttribute("id"),eElement.getAttribute("partialTriggers")));
                    //System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
                    //System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
                    //System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
                    NodeList pflList = ((Element) eElement).getElementsByTagName("af:panelFormLayout");
                    System.out.println(pflList.getLength());
                    for (int j = 0; j < pflList.getLength(); j++) {
                        Node pflNode = pflList.item(j);
                        System.out.println("\nCurrent Element :" + pflNode.getNodeName());
                        if (pflNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element pflElement=(Element)pflNode;
                            System.out.println("PanelFormLayout id : " + pflElement.getAttribute("id"));
                            System.out.println("Partial trigger on PFL: " + pflElement.getAttribute("partialTriggers"));
                            panelFormContainerList.add(
                                    new PanelFormContainer(pflElement.getAttribute("id"), pflElement.getAttribute("partialTriggers"))
                            );
                            NodeList plmList = ((Element) pflElement).getElementsByTagName("af:panelLabelAndMessage");
                            for (int i = 0; i < plmList.getLength(); i++) {
                                Node plmNode = plmList.item(i);
                                System.out.println("\nCurrent Element :" + plmNode.getNodeName());
                                if (plmNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element pglElem = (Element) plmNode;
                                    System.out.println("panelLabelAndMessage id : " + pglElem.getAttribute("id"));
                                    System.out.println("Label : " + pglElem.getAttribute("label"));
                                    //partialTriggerToLabel.put(pglElem.getAttribute("partialTrigger"), pglElem.getAttribute("label"));
                                    String partialTrig=pglElem.getAttribute("partialTriggers");
                                    String[] str=partialTrig.split(" ");
                                    StringBuilder sb= new StringBuilder();
                                    for(int t=0;t<str.length;t++)
                                    {
                                        sb.append(str[t]);
                                        sb.append("[");
                                        sb.append(partialTriggerToLabel.get(str[t]));
                                        sb.append("]");
                                    }
                                    panelLabelMessageList.add(
                                            new PanelLabelMessage(pglElem.getAttribute("id"),
                                                    pglElem.getAttribute("label"),
                                                    sb.toString())
                                    );
                                NodeList sipList= ((Element) pglElem).getElementsByTagName("fnd:secureInputSearch");
                                for(int k=0;k<sipList.getLength();k++) {
                                    Node sipNode = sipList.item(k);
                                    if (sipNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element sipElem = (Element) sipNode;
                                        componentList.add(new Component
                                                (sipElem.getAttribute("id"), sipElem.getAttribute("label"), sipElem.getAttribute("partialTrigger")));
                                        if (sipElem != null) {
                                            NodeList isList = ((Element) pglElem).getElementsByTagName("af:inputSearch");
                                            Node isNode = isList.item(0);
                                            if (isNode.getNodeType() == Node.ELEMENT_NODE) {
                                                Element isElem = (Element) isNode;
                                                partialTriggerToLabel.put(sipElem.getAttribute("id")+":"+isElem.getAttribute("id"),
                                                        isElem.getAttribute("label"));
                                                componentList.add(
                                                        new Component(isElem.getAttribute("id"), isElem.getAttribute("label"), isElem.getAttribute("partialTrigger"))
                                                );
                                            }
                                        }
                                    }
                                }
                                }

                            }
                            NodeList descFlexList = ((Element) pflElement).getElementsByTagName("fnd:descriptiveFlexfield");
                            for (int i = 0; i < descFlexList.getLength(); i++) {
                                Node descFlexNode = descFlexList.item(i);
                                System.out.println("\nCurrent Element :" + descFlexNode.getNodeName());
                                if (descFlexNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element descFlexElem = (Element) descFlexNode;
                                    System.out.println("descriptiveFlexfield id : " + descFlexElem.getAttribute("id"));
                                    //System.out.println("Label : " + descFlexElem.getAttribute("label"));
                                }
                            }
                            NodeList selectOneList = ((Element) pflElement).getElementsByTagName("af:selectOneChoice");
                            for (int i = 0; i < selectOneList.getLength(); i++) {
                                Node selectOneNode = selectOneList.item(i);
                                System.out.println("\nCurrent Element :" + selectOneNode.getNodeName());
                                if (selectOneNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element selectOneElem = (Element) selectOneNode;
                                    System.out.println("selectOneChoice id : " + selectOneElem.getAttribute("id"));
                                    //partialTriggerToLabel.put(selectOneElem.getAttribute("partialTriggers"), selectOneElem.getAttribute(("label")));

                                    //System.out.println("Label : " + descFlexElem.getAttribute("label"));
                                    String partialTrig=selectOneElem.getAttribute("partialTriggers");
                                    String[] str=partialTrig.split(" ");
                                    StringBuilder sb= new StringBuilder();
                                    for(int t=0;t<str.length;t++)
                                    {
                                        sb.append(str[t]);
                                        sb.append("[");
                                        sb.append(partialTriggerToLabel.get(str[t]));
                                        sb.append("]");
                                    }
                                    componentList.add(
                                            new Component(selectOneElem.getAttribute("id"),
                                                    selectOneElem.getAttribute("label"),
                                                    sb.toString())
                                    );
                                }
                            }
                            NodeList inputDateList = ((Element) pflElement).getElementsByTagName("af:inputDate");
                            for (int i = 0; i < inputDateList.getLength(); i++) {
                                Node inputDateNode = inputDateList.item(i);
                                System.out.println("\nCurrent Element :" + inputDateNode.getNodeName());
                                if (inputDateNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element inputDateElem = (Element) inputDateNode;
                                    System.out.println("InputDate id : " + inputDateElem.getAttribute("id"));
                                    //partialTriggerToLabel.put(inputDateElem.getAttribute("partialTriggers"), inputDateElem.getAttribute("label"));

                                    //System.out.println("Label : " + descFlexElem.getAttribute("label"));
                                    String partialTrig=inputDateElem.getAttribute("partialTriggers");
                                    String[] str=partialTrig.split(" ");
                                    StringBuilder sb= new StringBuilder();
                                    for(int t=0;t<str.length;t++)
                                    {
                                        sb.append(str[t]);
                                        sb.append("[");
                                        sb.append(partialTriggerToLabel.get(str[t]));
                                        sb.append("]");
                                    }
                                    componentList.add(
                                            new Component(inputDateElem.getAttribute("id"),
                                                    inputDateElem.getAttribute("label"),
                                                    sb.toString())
                                    );
                                }
                            }
                            NodeList inputTextList = ((Element) pflElement).getElementsByTagName("af:inputText");
                            for (int i = 0; i < inputTextList.getLength(); i++) {
                                Node inputTextNode = inputTextList.item(i);
                                System.out.println("\nCurrent Element :" + inputTextNode.getNodeName());
                                if (inputTextNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element inputTextElem = (Element) inputTextNode;
                                    System.out.println("InputText id : " + inputTextElem.getAttribute("id"));
                                    //partialTriggerToLabel.put(inputTextElem.getAttribute("partialTriggers"),inputTextElem.getAttribute("label"));

                                    //System.out.println("Label : " + descFlexElem.getAttribute("label"));
                                    String partialTrig=inputTextElem.getAttribute("partialTriggers");
                                    String[] str=partialTrig.split(" ");
                                    StringBuilder sb= new StringBuilder();
                                    for(int t=0;t<str.length;t++)
                                    {
                                        sb.append(str[t]);
                                        sb.append("[");
                                        sb.append(partialTriggerToLabel.get(str[t]));
                                        sb.append("]");
                                    }
                                    componentList.add(
                                            new Component(inputTextElem.getAttribute("id"),
                                                    inputTextElem.getAttribute("label"),
                                                    sb.toString())
                                    );
                                }
                            }
                        }
                    }
                }
            }


            //for resignationflex
            StringBuilder html=new StringBuilder();
            html.append("<html>" +
                    "<body>" +
                    "<table>" +
                    "<tr>" +
                    "<th>PanelFormLayout</th>" +
                    "<th>PanelLabelAndMessage</th>" +
                    "<th>id</th>" +
                    "</tr>");
            for (int temp = 0; temp < panelFlList.getLength(); temp++) {

                Node pflNode = panelFlList.item(temp);

                System.out.println("\nCurrent Element :" + pflNode.getNodeName());
                if (pflNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element pflElem = (Element) pflNode;

                    System.out.println("PanelFormLayout id : " + pflElem.getAttribute("id"));
                    System.out.println("Partial Trigger : " + pflElem.getAttribute("partialTriggers"));
                    NodeList plmList = ((Element) pflElem).getElementsByTagName("af:panelLabelAndMessage");
                    for (int i = 0; i < plmList.getLength(); i++) {
                        Node plmNode = plmList.item(i);
                        System.out.println("\nCurrent Element :" + plmNode.getNodeName());
                        if (plmNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element pglElem = (Element) plmNode;
                            html.append("<tr><td>")
                                    .append(pflNode.getNodeName())
                                    .append("<tr><td>").append(plmNode.getNodeName())
                                    .append("<tr><td>").append(pglElem.getAttribute("id"));
                            System.out.println("id : " + pglElem.getAttribute("id"));
                            System.out.println("Label : " + pglElem.getAttribute("label"));
                            panelLabelMessageList.add(
                                    new PanelLabelMessage(pglElem.getAttribute("id"),
                                            pglElem.getAttribute("label"),
                                            pglElem.getAttribute("partialTriggers"))
                            );
                        }
                    }
                    NodeList descFlexList = ((Element) pflElem).getElementsByTagName("fnd:descriptiveFlexfield");
                    for (int i = 0; i < descFlexList.getLength(); i++) {
                        Node descFlexNode = descFlexList.item(i);
                        System.out.println("\nCurrent Element :" + descFlexNode.getNodeName());
                        if (descFlexNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element descFlexElem = (Element) descFlexNode;
                            System.out.println("id : " + descFlexElem.getAttribute("id"));
                            //System.out.println("Label : " + descFlexElem.getAttribute("label"));
                        }
                    }
                    NodeList selectOneList = ((Element) pflElem).getElementsByTagName("af:selectOneChoice");
                    for (int i = 0; i < selectOneList.getLength(); i++) {
                        Node selectOneNode = selectOneList.item(i);
                        System.out.println("\nCurrent Element :" + selectOneNode.getNodeName());
                        if (selectOneNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element selectOneElem = (Element) selectOneNode;
                            System.out.println("id : " + selectOneElem.getAttribute("id"));
                            partialTriggerToLabel.put(selectOneElem.getAttribute("partialTriggers"), selectOneElem.getAttribute(("label")));
                            //System.out.println("Label : " + descFlexElem.getAttribute("label"));
                            componentList.add(
                                    new Component(selectOneElem.getAttribute("id"),
                                            selectOneElem.getAttribute("label"),
                                            selectOneElem.getAttribute("partialTriggers"))
                            );
                        }
                    }
                    NodeList inputDateList = ((Element) pflElem).getElementsByTagName("af:inputDate");
                    for (int i = 0; i < inputDateList.getLength(); i++) {
                        Node inputDateNode = inputDateList.item(i);
                        System.out.println("\nCurrent Element :" + inputDateNode.getNodeName());
                        if (inputDateNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element inputDateElem = (Element) inputDateNode;
                            System.out.println("InputDate id : " + inputDateElem.getAttribute("id"));
                            partialTriggerToLabel.put(inputDateElem.getAttribute("partialTriggers"), inputDateElem.getAttribute("label"));
                            //System.out.println("Label : " + descFlexElem.getAttribute("label"));
                            componentList.add(
                                    new Component(inputDateElem.getAttribute("id"),
                                            inputDateElem.getAttribute("label"),
                                            inputDateElem.getAttribute("partialTriggers"))
                            );
                        }
                    }
                    NodeList inputTextList = ((Element) pflElem).getElementsByTagName("af:inputText");
                    for (int i = 0; i < inputTextList.getLength(); i++) {
                        Node inputTextNode = inputTextList.item(i);
                        System.out.println("\nCurrent Element :" + inputTextNode.getNodeName());
                        if (inputTextNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element inputTextElem = (Element) inputTextNode;
                            System.out.println("InputText id : " + inputTextElem.getAttribute("id"));
                            //System.out.println("Label : " + descFlexElem.getAttribute("label"));
                            partialTriggerToLabel.put(inputTextElem.getAttribute("partialTriggers"),inputTextElem.getAttribute("label"));
                            componentList.add(
                                    new Component(inputTextElem.getAttribute("id"),
                                            inputTextElem.getAttribute("label"),
                                            inputTextElem.getAttribute("partialTriggers"))
                            );
                        }
                    }
                }
            }
            FileWriter file= new FileWriter("/Users/aaehsan/Downloads/GHRIDExtractor/src/resources/output.html");
            PrintWriter pw=new PrintWriter(file);
            pw.print(html.toString());
            pw.close();
            System.out.println(html);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
