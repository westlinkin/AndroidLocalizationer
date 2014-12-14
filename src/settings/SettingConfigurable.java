/*
 * Copyright 2014 Wesley Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import data.Log;
import data.StorageDataKey;
import language_engine.TranslationEngineType;
import module.FilterRule;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Wesley Lin on 12/8/14.
 */
public class SettingConfigurable implements Configurable, ActionListener {

    private static final String DEFAULT_CLIENT_ID = "Default client id";
    private static final String DEFAULT_CLIENT_SECRET = "Default client secret";

    private JPanel settingPanel;
    private JComboBox languageEngineBox;
    private TranslationEngineType currentEngine;

    private Container bingContainer;
    private JTextField bingClientIdField;
    private JTextField bingClientSecretField;

    private JBList filterList;
    private JButton btnAddFilter;
    private JButton btnDeleteFilter;

    private java.util.List<FilterRule> filterRules = new ArrayList<FilterRule>();
    private boolean languageEngineChanged = false;
    private boolean filterRulesChanged = false;

    @Nls
    @Override
    public String getDisplayName() {
        return "Android Localizationer";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getDisplayName();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (settingPanel == null) {
            settingPanel = new JPanel(new VerticalLayout(18));

            // header UI
            Container container = new Container();
            container.setLayout(new BorderLayout());

            // todo: only one language engine for now, will add a few more in the future
            currentEngine = TranslationEngineType.fromName(
                    PropertiesComponent.getInstance().getValue(StorageDataKey.SettingLanguageEngine));
            TranslationEngineType[] items = TranslationEngineType.getLanguageEngineArray();
            languageEngineBox = new ComboBox(items);
            languageEngineBox.setEnabled(true);
            languageEngineBox.setSelectedItem(currentEngine);
            languageEngineBox.addActionListener(this);

            container.add(new JLabel("Language engine: "), BorderLayout.WEST);
            container.add(languageEngineBox, BorderLayout.CENTER);

            settingPanel.add(container);

            // todo: at first, only bing, add a function: initUI(TranslationEngineType)
            initBingContainer();
            settingPanel.add(bingContainer);

            initAndAddFilterContainer();
        }
        return settingPanel;
    }

    @Override
    public boolean isModified() {
        if (languageEngineChanged)
            return true;

        if (filterRulesChanged)
            return true;

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        switch (currentEngine) {
            case Bing: {
                String bingClientIdStored = propertiesComponent.getValue(StorageDataKey.BingClientIdStored);
                String bingClientSecretStored = propertiesComponent.getValue(StorageDataKey.BingClientSecretStored);

                boolean bingClientIdChanged = false;
                boolean bingClientSecretChanged = false;

                if (bingClientIdStored == null) {
                    if (!bingClientIdField.getText().isEmpty())
                        bingClientIdChanged = true;
                } else {
                    if (!bingClientIdField.getText().equals(bingClientIdStored)
                            && !bingClientIdField.getText().trim().isEmpty())
                        bingClientIdChanged = true;
                }

                if (bingClientSecretStored == null) {
                    if (!bingClientSecretField.getText().isEmpty())
                        bingClientSecretChanged = true;
                } else {
                    if (!bingClientSecretField.getText().equals(bingClientSecretStored)
                            && !bingClientSecretField.getText().trim().isEmpty())
                        bingClientSecretChanged = true;
                }

                return bingClientIdChanged || bingClientSecretChanged;
            }
            case Google: {
                //todo: add google
                return false;
            }
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        Log.i("apply clicked");
        if (languageEngineBox == null || filterList == null
                || btnAddFilter == null || btnDeleteFilter == null)
            return;

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

        languageEngineChanged = false;
        propertiesComponent.setValue(StorageDataKey.SettingLanguageEngine, currentEngine.toName());

        switch (currentEngine) {
            case Bing: {
                if (bingClientIdField == null || bingClientSecretField == null)
                    return;

                if (!bingClientIdField.getText().trim().isEmpty()) {
                    propertiesComponent.setValue(StorageDataKey.BingClientIdStored, bingClientIdField.getText());
                    PromptSupport.setPrompt(bingClientIdField.getText(), bingClientIdField);
                }

                if (!bingClientSecretField.getText().trim().isEmpty()) {
                    propertiesComponent.setValue(StorageDataKey.BingClientSecretStored, bingClientSecretField.getText());
                    PromptSupport.setPrompt(bingClientSecretField.getText(), bingClientSecretField);
                }
                bingClientIdField.setText("");
                bingClientSecretField.setText("");
            }
            break;
            case Google: {
                //todo: add google
            }
            break;
        }
        languageEngineBox.requestFocus();

        // todo store filter rules, write filterRules
        filterRulesChanged = false;

    }

    @Override
    public void reset() {
        if (settingPanel == null || languageEngineBox == null || filterList == null
                || btnAddFilter == null || btnDeleteFilter == null)
            return;
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

        currentEngine = TranslationEngineType.fromName(
                propertiesComponent.getValue(StorageDataKey.SettingLanguageEngine));
        languageEngineBox.setSelectedItem(currentEngine);
        languageEngineChanged = false;
        Log.i("reset, current engine: " + currentEngine);

        switch (currentEngine) {
            case Bing: {
                if (bingClientIdField == null || bingClientSecretField == null)
                    return;

                String bingClientIdStored = propertiesComponent.getValue(StorageDataKey.BingClientIdStored);
                String bingClientSecretStored = propertiesComponent.getValue(StorageDataKey.BingClientSecretStored);

                if (bingClientIdStored != null) {
                    PromptSupport.setPrompt(bingClientIdStored, bingClientIdField);
                } else {
                    PromptSupport.setPrompt(DEFAULT_CLIENT_ID, bingClientIdField);
                }
                bingClientIdField.setText("");

                if (bingClientSecretStored != null) {
                    PromptSupport.setPrompt(bingClientSecretStored, bingClientSecretField);
                } else {
                    PromptSupport.setPrompt(DEFAULT_CLIENT_SECRET, bingClientSecretField);
                }
                bingClientSecretField.setText("");
            }
            break;
            case Google: {

            }
            break;
        }
        languageEngineBox.requestFocus();

        // filter rules
        filterRulesChanged = false;
        resetFilterList();
    }

    @Override
    public void disposeUIResources() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox comboBox = (JComboBox) e.getSource();
        TranslationEngineType type = (TranslationEngineType) comboBox.getSelectedItem();
        if (type == currentEngine)
            return;

        languageEngineChanged = true;
        Log.i("selected type: " + type.name());
        currentEngine = type;

        //todo: change other JComponents
        // currentEngine = ...

    }

    private void initBingContainer() {
        if (bingContainer != null)
            return;

        bingClientIdField = new JTextField();
        bingClientSecretField = new JTextField();

        PromptSupport.setPrompt(DEFAULT_CLIENT_ID, bingClientIdField);
        PromptSupport.setPrompt(DEFAULT_CLIENT_SECRET, bingClientSecretField);

        bingContainer = new Container();
        bingContainer.setLayout(new BorderLayout(0, 5));

        String howto = "<html><a href=\"http://blogs.msdn.com/b/translation/p/gettingstarted1.aspx\">How to get ClientId and ClientSecret?</a></html>";
        JLabel howtoLabel = new JLabel();
        howtoLabel.setText(howto);
        howtoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        howtoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://blogs.msdn.com/b/translation/p/gettingstarted1.aspx"));
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        bingContainer.add(howtoLabel, BorderLayout.NORTH);

        Container contentContainer = new Container();
        contentContainer.setLayout(new GridBagLayout());
        ((GridBagLayout)contentContainer.getLayout()).columnWidths = new int[] {0, 0, 0};
        ((GridBagLayout)contentContainer.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)contentContainer.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
        ((GridBagLayout)contentContainer.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

        contentContainer.add(new JLabel("<html><br></html>"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        JLabel clientIdLabel = new JLabel("Client Id:");
        clientIdLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        contentContainer.add(clientIdLabel, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        contentContainer.add(bingClientIdField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        JLabel clientSecretLabel = new JLabel("Client Secret:");
        clientSecretLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        contentContainer.add(clientSecretLabel, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        contentContainer.add(bingClientSecretField, new GridBagConstraints(1, 1, 1, 1, 10.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        bingContainer.add(contentContainer, BorderLayout.CENTER);
    }

    private void initAndAddFilterContainer() {
        Container filterSettingContainer = new Container();
        filterSettingContainer.setLayout(new BorderLayout(0, 5));

        final JLabel filterLabel = new JLabel("Filter setting");
        filterSettingContainer.add(filterLabel, BorderLayout.NORTH);

        {
            Container listPane = new Container();
            listPane.setLayout(new BorderLayout());

            JBScrollPane scrollPane = new JBScrollPane();
            filterList = new JBList(new String[]{"1," , "2"});

            filterList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            scrollPane.setViewportView(filterList);
            listPane.add(scrollPane, BorderLayout.NORTH);

            Container btnPane = new Container();
            btnPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            btnAddFilter = new JButton("+");
            btnDeleteFilter = new JButton("-");
            btnPane.add(btnAddFilter);
            btnPane.add(btnDeleteFilter);

            filterList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (filterList.getSelectedIndex() <= 0) {
                            btnDeleteFilter.setEnabled(false);
                        } else {
                            btnDeleteFilter.setEnabled(true);
                        }
                    }
                }
            });

            btnAddFilter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    filterRulesChanged = true;
                    // todo
                    Messages.showErrorDialog(settingPanel, "error");
                }
            });

            btnDeleteFilter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    filterRulesChanged = true;
                    int index = filterList.getSelectedIndex();
                    filterRules.remove(index);
                    filterList.setListData(getFilterRulesDisplayString());
                    if (index < filterRules.size()) {
                        filterList.setSelectedIndex(index);
                    } else {
                        if (filterRules.size() == 1) {
                            btnDeleteFilter.setEnabled(false);
                        }
                        filterList.setSelectedIndex(filterRules.size() - 1);
                    }
                }
            });

            listPane.add(btnPane, BorderLayout.CENTER);
            filterSettingContainer.add(listPane, BorderLayout.CENTER);
        }
        settingPanel.add(filterSettingContainer);
    }

    private void resetFilterList() {
        btnDeleteFilter.setEnabled(false);
        filterRules.clear();
        filterRules.addAll(FilterRule.getFilterRulesFromLocal());

        filterList.setListData(getFilterRulesDisplayString());
    }

    private String[] getFilterRulesDisplayString() {
        String[] displayStrings = new String[filterRules.size()];
        for (int i = 0; i < filterRules.size(); i++ ) {
            displayStrings[i] = filterRules.get(i).toString();
        }
        return displayStrings;
    }
}
