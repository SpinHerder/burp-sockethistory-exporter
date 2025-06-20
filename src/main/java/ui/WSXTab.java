package ui;

import data.WebSocketMessageData;
import exporter.ExportConfiguration;
import exporter.WebSocketExporter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WSXTab extends JPanel
{
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private final List<WebSocketMessageData> messages;
    private final DefaultTableModel tableModel;
    private final JTable messageTable;
    
    private JTextField clientToServerField;
    private JTextField serverToClientField;
    private JCheckBox includeTimestampCheckBox;
    private JCheckBox xmlFormatCheckBox;
    private JCheckBox jsonFormatCheckBox;
    private JTextField filePathField;
    private JButton browseButton;
    private JButton exportButton;
    private JButton clearButton;

    public WSXTab()
    {
        this.messages = new ArrayList<>();
        
        setLayout(new BorderLayout());
        
        String[] columnNames = {"Direction", "Timestamp", "Length", "Message Preview", "URL"};
        tableModel = new DefaultTableModel(columnNames, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        
        messageTable = new JTable(tableModel);
        messageTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        messageTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        
        JScrollPane tableScrollPane = new JScrollPane(messageTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));
        
        JPanel exportPanel = createExportConfigurationPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, exportPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.7);
        
        add(splitPane, BorderLayout.CENTER);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("WebSocket Messages:"));
        JLabel countLabel = new JLabel("0 messages");
        headerPanel.add(countLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        tableModel.addTableModelListener(e -> {
            countLabel.setText(tableModel.getRowCount() + " messages");
        });
    }

    private JPanel createExportConfigurationPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Export Configuration"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        clientToServerField = new JTextField("C-S", 15);
        serverToClientField = new JTextField("S-C", 15);
        includeTimestampCheckBox = new JCheckBox("Include timestamps", true);
        xmlFormatCheckBox = new JCheckBox("XML format", true);
        jsonFormatCheckBox = new JCheckBox("JSON format", false);
        filePathField = new JTextField(25);
        browseButton = new JButton("Browse...");
        exportButton = new JButton("Export Messages");
        clearButton = new JButton("Clear List");

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Client to Server label:"), gbc);
        gbc.gridx = 1;
        panel.add(clientToServerField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Server to Client label:"), gbc);
        gbc.gridx = 3;
        panel.add(serverToClientField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(includeTimestampCheckBox, gbc);
        gbc.gridx = 1;
        panel.add(xmlFormatCheckBox, gbc);
        gbc.gridx = 2;
        panel.add(jsonFormatCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Export file:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(filePathField, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(browseButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(clearButton);
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, gbc);

        setupEventHandlers();

        return panel;
    }

    private void setupEventHandlers()
    {
        xmlFormatCheckBox.addActionListener(e -> {
            if (xmlFormatCheckBox.isSelected()) {
                jsonFormatCheckBox.setSelected(false);
            }
        });
        
        jsonFormatCheckBox.addActionListener(e -> {
            if (jsonFormatCheckBox.isSelected()) {
                xmlFormatCheckBox.setSelected(false);
            }
        });

        browseButton.addActionListener(new BrowseButtonListener());
        exportButton.addActionListener(new ExportButtonListener());
        clearButton.addActionListener(e -> clearMessages());
    }

    public void addMessages(List<WebSocketMessageData> newMessages)
    {
        messages.addAll(newMessages);
        
        for (WebSocketMessageData message : newMessages)
        {
            String direction = message.isClientToServer() ? "C-S" : "S-C";
            String timestamp = message.getTimestamp().format(TIMESTAMP_FORMATTER);
            String preview = message.getMessage().length() > 100 
                ? message.getMessage().substring(0, 100) + "..." 
                : message.getMessage();
            
            Object[] row = {direction, timestamp, message.getMessage().length(), preview, message.getUpgradeRequestUrl()};
            tableModel.addRow(row);
        }
        
        SwingUtilities.invokeLater(() -> {
            int lastRow = messageTable.getRowCount() - 1;
            if (lastRow >= 0) {
                messageTable.scrollRectToVisible(messageTable.getCellRect(lastRow, 0, true));
            }
        });
    }

    private void clearMessages()
    {
        messages.clear();
        tableModel.setRowCount(0);
    }

    private class BrowseButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save WebSocket History");
            
            if (xmlFormatCheckBox.isSelected()) {
                fileChooser.setFileFilter(new FileNameExtensionFilter("XML files (*.xml)", "xml"));
                fileChooser.setSelectedFile(new File("websocket_history.xml"));
            } else if (jsonFormatCheckBox.isSelected()) {
                fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files (*.json)", "json"));
                fileChooser.setSelectedFile(new File("websocket_history.json"));
            }
            
            int result = fileChooser.showSaveDialog(WSXTab.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }
    }

    private class ExportButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (!validateInput()) {
                return;
            }

            if (messages.isEmpty()) {
                JOptionPane.showMessageDialog(WSXTab.this, 
                    "No messages to export. Use 'Send to WSX' from WebSocket context menu to add messages.", 
                    "No Messages", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                ExportConfiguration config = new ExportConfiguration(
                    clientToServerField.getText(),
                    serverToClientField.getText(),
                    includeTimestampCheckBox.isSelected(),
                    xmlFormatCheckBox.isSelected(),
                    filePathField.getText()
                );

                WebSocketExporter exporter = new WebSocketExporter();
                exporter.export(messages, config);
                
                JOptionPane.showMessageDialog(WSXTab.this, 
                    "WebSocket history exported successfully!\n" + messages.size() + " messages exported to:\n" + config.getFilePath(), 
                    "Export Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(WSXTab.this, 
                    "Error exporting WebSocket history: " + ex.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput()
    {
        if (clientToServerField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Client to Server label cannot be empty");
            return false;
        }
        
        if (serverToClientField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Server to Client label cannot be empty");
            return false;
        }
        
        if (!xmlFormatCheckBox.isSelected() && !jsonFormatCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please select either XML or JSON format");
            return false;
        }
        
        if (filePathField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an export file");
            return false;
        }
        
        return true;
    }
} 