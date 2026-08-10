package com.sunrisedentalclinic.client.ui.panels;

import com.sunrisedentalclinic.client.ApiClient;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class HelpPanel extends JPanel {

    private final ApiClient apiClient;
    private final DefaultListModel<String> topicListModel = new DefaultListModel<>();
    private final JList<String> topicList = new JList<>(topicListModel);
    private final JTextArea contentArea = new JTextArea();
    private final JLabel statusLabel = new JLabel(" ");

    public HelpPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Help");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        topicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane topicScroll = new JScrollPane(topicList);
        topicScroll.setPreferredSize(new Dimension(220, 0));

        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane contentScroll = new JScrollPane(contentArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, topicScroll, contentScroll);
        splitPane.setDividerLocation(220);
        add(splitPane, BorderLayout.CENTER);

        statusLabel.setForeground(Color.RED);
        add(statusLabel, BorderLayout.SOUTH);

        topicList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = topicList.getSelectedValue();
                if (selected != null) {
                    loadContent(selected);
                }
            }
        });

        loadTopics();
    }

    public void refresh() {
        loadTopics();
    }

    @SuppressWarnings("unchecked")
    private void loadTopics() {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Loading topics...");
        contentArea.setText("Select a topic on the left to view help.");

        new SwingWorker<Void, Void>() {
            int status;
            List<String> topics;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<List> resp = apiClient.getHelpTopics();
                    status = resp.statusCode;
                    if (status == 200 && resp.body != null) {
                        topics = resp.body;
                    } else {
                        errorMessage = resp.errorMessage != null ? resp.errorMessage : "Failed to load help topics.";
                    }
                } catch (Exception ex) {
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (errorMessage != null) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage);
                    return;
                }
                topicListModel.clear();
                for (String topic : topics) {
                    topicListModel.addElement(topic);
                }
                statusLabel.setForeground(new Color(0, 128, 0));
                statusLabel.setText(topics.size() + " help topic(s) available.");
                if (!topicListModel.isEmpty()) {
                    topicList.setSelectedIndex(0);
                }
            }
        }.execute();
    }

    @SuppressWarnings("unchecked")
    private void loadContent(String topic) {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText("Loading...");
        contentArea.setText("");

        new SwingWorker<Void, Void>() {
            int status;
            String content;
            String errorMessage;

            @Override
            protected Void doInBackground() {
                try {
                    ApiClient.ApiResponse<Map> resp = apiClient.getHelpContent(topic);
                    status = resp.statusCode;
                    if (status == 200 && resp.body != null) {
                        Object c = resp.body.get("content");
                        content = c != null ? c.toString() : "No help content found for this topic.";
                    } else {
                        errorMessage = resp.errorMessage != null ? resp.errorMessage : "Failed to load help content.";
                    }
                } catch (Exception ex) {
                    errorMessage = "Error contacting server: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (errorMessage != null) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage);
                    return;
                }
                contentArea.setText(content);
                contentArea.setCaretPosition(0);
                statusLabel.setText(" ");
            }
        }.execute();
    }
}