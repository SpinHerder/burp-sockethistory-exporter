package ui;

import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.WebSocketContextMenuEvent;
import data.WebSocketMessageData;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static burp.WebSocketHistoryExporter.EXTENSION_NAME;

public class WSXMenuProvider implements ContextMenuItemsProvider
{
    private final WSXTab wsxTab;

    public WSXMenuProvider(WSXTab wsxTab)
    {
        this.wsxTab = wsxTab;
    }

    @Override
    public List<Component> provideMenuItems(WebSocketContextMenuEvent event)
    {
        JMenuItem sendToWSXMenuItem = new JMenuItem("Send to " + EXTENSION_NAME);
        sendToWSXMenuItem.addActionListener(l -> performSendToWSX(event));

        return List.of(sendToWSXMenuItem);
    }

    private void performSendToWSX(WebSocketContextMenuEvent event)
    {
        List<WebSocketMessageData> messages = event.selectedWebSocketMessages().stream()
                .map(WebSocketMessageData::from)
                .toList();

        if (!messages.isEmpty())
        {
            wsxTab.addMessages(messages);
            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, 
                    messages.size() + " WebSocket message(s) added to WSX tab", 
                    "Messages Added", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }
} 