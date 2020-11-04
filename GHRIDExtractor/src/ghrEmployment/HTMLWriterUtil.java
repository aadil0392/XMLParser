package ghrEmployment;

import model.Component;
import model.PanelFormContainer;
import model.PanelLabelMessage;

import java.io.*;
import java.net.URL;
import java.util.List;

public class HTMLWriterUtil {

    public URL getHtmlReportUrl(String htmlFileName) {
        URL url = this.getClass().getClassLoader().getResource(htmlFileName);
        return url;
    }

    public void fileWrite(List<Component> componentList, List<PanelFormContainer> panelFormContainerList, List<PanelLabelMessage> panelLabelMessageList) throws IOException {
        File htmlTemplateFile = new File("template.html");
        String htmlString = readFromFile(htmlTemplateFile.getName());
        StringBuilder plmBuffer = new StringBuilder();
        StringBuilder componentBuffer = new StringBuilder();
        StringBuilder pflBuffer = new StringBuilder();
        panelLabelMessageList.forEach(h->{
            plmBuffer.append("<tr><td><b>")
                    .append(h.getId())
                    .append("</b></td><td>")
                    .append(h.getLabel())
                    .append("</td><td>")
                    .append(h.getPartialTrigger())
                    .append("</td></tr>");
        });
        componentList.forEach(h->{
            componentBuffer.append("<tr><td><b>")
                    .append(h.getId())
                    .append("</b></td><td>")
                    .append(h.getLabel())
                    .append("</td><td>")
                    .append(h.getPartialTrigger())
                    .append("</td></tr>");
        });
        panelFormContainerList.forEach(h->{
            pflBuffer.append("<tr><td><b>")
                    .append(h.getId())
                    .append("</b></td><td>")
                    .append(h.getPartialTrigger())
                    .append("</td></tr>");
        });
        htmlString = htmlString.replace("$plm", plmBuffer.toString());
        htmlString = htmlString.replace("$component", componentBuffer.toString());
        htmlString = htmlString.replace("$pfl", pflBuffer.toString());
        FileWriter fw=new FileWriter("Extractor_"+ java.util.Calendar.getInstance().getTime()+".html");
        fw.write(htmlString);
        fw.close();

    }
    public String readFromFile(String filename) {
        InputStream inputStream = null;
        String fileData = "";
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("resources/"+filename);
            fileData = readFromInputStream(inputStream);
        } catch (IOException e) {
            // Add Exception class
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Add Exception class
                e.printStackTrace();
            }
        }
        return fileData;

    }

    public String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuffer data = new StringBuffer();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                data = data.append(line);
            }
        }
        return data.toString();
    }
}
