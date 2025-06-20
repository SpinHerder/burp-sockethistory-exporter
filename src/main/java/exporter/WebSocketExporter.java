package exporter;

import data.WebSocketMessageData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WebSocketExporter
{
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public void export(List<WebSocketMessageData> messages, ExportConfiguration config) throws IOException
    {
        if (config.isXmlFormat())
        {
            exportToXml(messages, config);
        }
        else
        {
            exportToJson(messages, config);
        }
    }

    private void exportToXml(List<WebSocketMessageData> messages, ExportConfiguration config) throws IOException
    {
        try (FileWriter writer = new FileWriter(config.getFilePath()))
        {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<websocket-history>\n");

            for (WebSocketMessageData message : messages)
            {
                writer.write("  <message>\n");
                
                String direction = message.isClientToServer() 
                    ? config.getClientToServerLabel() 
                    : config.getServerToClientLabel();
                writer.write("    <direction>" + direction + "</direction>\n");
                
                if (config.isIncludeTimestamp())
                {
                    writer.write("    <timestamp>" + message.getTimestamp().format(TIMESTAMP_FORMATTER) + "</timestamp>\n");
                }
                
                writer.write("    <content>");
                writer.write(message.getMessage());
                writer.write("</content>\n");
                
                writer.write("    <url>" + escapeXml(message.getUpgradeRequestUrl()) + "</url>\n");
                writer.write("  </message>\n");
            }

            writer.write("</websocket-history>\n");
        }
    }

    private void exportToJson(List<WebSocketMessageData> messages, ExportConfiguration config) throws IOException
    {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");
        jsonBuilder.append("  \"websocket-history\": [\n");

        for (int i = 0; i < messages.size(); i++)
        {
            WebSocketMessageData message = messages.get(i);
            
            String direction = message.isClientToServer() 
                ? config.getClientToServerLabel() 
                : config.getServerToClientLabel();
            
            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"direction\": \"").append(escapeJsonString(direction)).append("\",\n");
            
            if (config.isIncludeTimestamp())
            {
                jsonBuilder.append("      \"timestamp\": \"").append(message.getTimestamp().format(TIMESTAMP_FORMATTER)).append("\",\n");
            }
            
            String messageContent = message.getMessage();
            jsonBuilder.append("      \"content\": ");
            
            try 
            {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonContent = mapper.readTree(messageContent);
                jsonBuilder.append(messageContent);
            } 
            catch (Exception e) 
            {
                jsonBuilder.append("\"").append(messageContent).append("\"");
            }
            
            jsonBuilder.append(",\n");
            jsonBuilder.append("      \"url\": \"").append(escapeJsonString(message.getUpgradeRequestUrl())).append("\"\n");
            jsonBuilder.append("    }");
            
            if (i < messages.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }

        jsonBuilder.append("  ]\n");
        jsonBuilder.append("}\n");

        try (FileWriter writer = new FileWriter(config.getFilePath()))
        {
            writer.write(jsonBuilder.toString());
        }
    }

    private String escapeJsonString(String text)
    {
        if (text == null)
        {
            return "";
        }
        
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private String escapeXml(String text)
    {
        if (text == null)
        {
            return "";
        }
        
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
} 