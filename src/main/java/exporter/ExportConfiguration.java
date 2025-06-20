package exporter;

public class ExportConfiguration
{
    private final String clientToServerLabel;
    private final String serverToClientLabel;
    private final boolean includeTimestamp;
    private final boolean xmlFormat;
    private final String filePath;

    public ExportConfiguration(String clientToServerLabel, String serverToClientLabel, 
                             boolean includeTimestamp, boolean xmlFormat, String filePath)
    {
        this.clientToServerLabel = clientToServerLabel;
        this.serverToClientLabel = serverToClientLabel;
        this.includeTimestamp = includeTimestamp;
        this.xmlFormat = xmlFormat;
        this.filePath = filePath;
    }

    public String getClientToServerLabel()
    {
        return clientToServerLabel;
    }

    public String getServerToClientLabel()
    {
        return serverToClientLabel;
    }

    public boolean isIncludeTimestamp()
    {
        return includeTimestamp;
    }

    public boolean isXmlFormat()
    {
        return xmlFormat;
    }

    public boolean isJsonFormat()
    {
        return !xmlFormat;
    }

    public String getFilePath()
    {
        return filePath;
    }
} 