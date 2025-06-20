package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.ui.UserInterface;
import ui.WSXMenuProvider;
import ui.WSXTab;

public class WebSocketHistoryExporter implements BurpExtension
{
    public static final String EXTENSION_NAME = "WSX";

    @Override
    public void initialize(MontoyaApi api)
    {
        Extension extension = api.extension();
        UserInterface userInterface = api.userInterface();

        WSXTab wsxTab = new WSXTab();
        
        userInterface.registerSuiteTab("WSX", wsxTab);

        userInterface.registerContextMenuItemsProvider(new WSXMenuProvider(wsxTab));

        extension.setName(EXTENSION_NAME);

        String extensionVersion = WebSocketHistoryExporter.class.getPackage().getImplementationVersion();
        api.logging().logToOutput("WSX v" + 
            (extensionVersion != null ? extensionVersion : "1.0.0") + " loaded");
    }
} 