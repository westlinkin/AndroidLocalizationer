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
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import data.Log;
import data.SerializeUtil;
import data.StorageDataKey;
import language_engine.TranslationEngineType;
import module.FilterRule;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import ui.AddFilterRuleDialog;

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

    private static final String DEFAULT_GOOGLE_API_KEY = "Enter API key here";

    private static final String BING_HOW_TO = "<html><a href=\"http://blogs.msdn.com/b/translation/p/gettingstarted1.aspx\">How to get ClientId and ClientSecret?</a></html>";
    private MouseAdapter bingHowTo = new MouseAdapter() {
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
    };

    private static final String GOOGLE_HOW_TO = "<html><a href=\"https://cloud.google.com/translate/v2/getting_started#intro\">How to set up Google Translation API key?</a></html>";
    private MouseAdapter googleHowTo = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                Desktop.getDesktop().browse(new URI("https://cloud.google.com/translate/v2/getting_started#intro"));
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };

    private JPanel settingPanel;
    private JComboBox languageEngineBox;
    private TranslationEngineType currentEngine;

    private JLabel howToLabel;
    private JLabel line1Text;
    private JTextField line1TextField;
    private JLabel line2Text;
    private JTextField line2TextField;

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

            initContentContainer();
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
                    if (!line1TextField.getText().isEmpty())
                        bingClientIdChanged = true;
                } else {
                    if (!line1TextField.getText().equals(bingClientIdStored)
                            && !line1TextField.getText().trim().isEmpty())
                        bingClientIdChanged = true;
                }

                if (bingClientSecretStored == null) {
                    if (!line2TextField.getText().isEmpty())
                        bingClientSecretChanged = true;
                } else {
                    if (!line2TextField.getText().equals(bingClientSecretStored)
                            && !line2TextField.getText().trim().isEmpty())
                        bingClientSecretChanged = true;
                }

                return bingClientIdChanged || bingClientSecretChanged;
            }
            case Google: {
                String googleApiKeyStored = propertiesComponent.getValue(StorageDataKey.GoogleApiKeyStored);
                boolean googleApiKeyStoredChanged = false;

                if (googleApiKeyStored == null) {
                    if (!line1TextField.getText().isEmpty())
                        googleApiKeyStoredChanged = true;
                } else {
                    if (!line1TextField.getText().equals(googleApiKeyStored)
                            && !line1TextField.getText().trim().isEmpty())
                        googleApiKeyStoredChanged = true;
                }
                return googleApiKeyStoredChanged;
            }
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        Log.i("apply clicked");
        if (languageEngineBox == null || filterList == null
                || btnAddFilter == null || btnDeleteFilter == null
                || line1TextField == null || line2TextField == null)
            return;

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

        languageEngineChanged = false;
        propertiesComponent.setValue(StorageDataKey.SettingLanguageEngine, currentEngine.toName());

        switch (currentEngine) {
            case Bing: {
                if (!line1TextField.getText().trim().isEmpty()) {
                    propertiesComponent.setValue(StorageDataKey.BingClientIdStored, line1TextField.getText());
                    PromptSupport.setPrompt(line1TextField.getText(), line1TextField);
                }

                if (!line2TextField.getText().trim().isEmpty()) {
                    propertiesComponent.setValue(StorageDataKey.BingClientSecretStored, line2TextField.getText());
                    PromptSupport.setPrompt(line2TextField.getText(), line2TextField);
                }
                line1TextField.setText("");
                line2TextField.setText("");
            }
            break;
            case Google: {
                if (!line1TextField.getText().trim().isEmpty()) {
                    propertiesComponent.setValue(StorageDataKey.GoogleApiKeyStored, line1TextField.getText());
                    PromptSupport.setPrompt(line1TextField.getText(), line1TextField);
                }
                line1TextField.setText("");
            }
            break;
        }
        languageEngineBox.requestFocus();

        filterRulesChanged = false;
        propertiesComponent.setValue(StorageDataKey.SettingFilterRules,
                SerializeUtil.serializeFilterRuleList(filterRules));
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
        initUI(currentEngine);

        Log.i("reset, current engine: " + currentEngine);

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
        if ((type == currentEngine) && (!languageEngineChanged))
            return;

        languageEngineChanged = true;
        Log.i("selected type: " + type.name());
        currentEngine = type;

        initUI(currentEngine);
    }

    private void initUI(TranslationEngineType engineType) {
        if (settingPanel == null)
            return;

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        switch (engineType) {
            case Bing: {
                line1Text.setText("Client Id:");
                line2Text.setText("Client secret:");
                line2Text.setVisible(true);

                line2TextField.setVisible(true);

                howToLabel.setText(BING_HOW_TO);
                howToLabel.removeMouseMotionListener(googleHowTo);
                howToLabel.addMouseListener(bingHowTo);

                String bingClientIdStored = propertiesComponent.getValue(StorageDataKey.BingClientIdStored);
                String bingClientSecretStored = propertiesComponent.getValue(StorageDataKey.BingClientSecretStored);

                if (bingClientIdStored != null) {
                    PromptSupport.setPrompt(bingClientIdStored, line1TextField);
                } else {
                    PromptSupport.setPrompt(DEFAULT_CLIENT_ID, line1TextField);
                }
                line1TextField.setText("");

                if (bingClientSecretStored != null) {
                    PromptSupport.setPrompt(bingClientSecretStored, line2TextField);
                } else {
                    PromptSupport.setPrompt(DEFAULT_CLIENT_SECRET, line2TextField);
                }
                line2TextField.setText("");
            }
            break;
            case Google: {
                line1Text.setText("API key:");
                line2Text.setVisible(false);

                line2TextField.setVisible(false);

                howToLabel.setText(GOOGLE_HOW_TO);
                howToLabel.removeMouseListener(bingHowTo);
                howToLabel.addMouseListener(googleHowTo);

                String googleAPIKey = propertiesComponent.getValue(StorageDataKey.GoogleApiKeyStored);

                if (googleAPIKey != null) {
                    PromptSupport.setPrompt(googleAPIKey, line1TextField);
                } else {
                    PromptSupport.setPrompt(DEFAULT_GOOGLE_API_KEY, line1TextField);
                }
                line1TextField.setText("");
            }
            break;
        }
    }

    private void initContentContainer() {
        line1TextField = new JTextField();
        line2TextField = new JTextField();

        line1Text = new JLabel("Client Id:");
        line2Text = new JLabel("Client Secret:");

        Container outContainer = new Container();
        outContainer.setLayout(new BorderLayout(0, 5));

        howToLabel = new JLabel();
        howToLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        outContainer.add(howToLabel, BorderLayout.NORTH);

        Container contentContainer = new Container();
        contentContainer.setLayout(new GridBagLayout());
        ((GridBagLayout)contentContainer.getLayout()).columnWidths = new int[] {0, 0, 0};
        ((GridBagLayout)contentContainer.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)contentContainer.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
        ((GridBagLayout)contentContainer.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

        line1Text.setHorizontalAlignment(SwingConstants.RIGHT);
        contentContainer.add(line1Text, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        contentContainer.add(line1TextField, new GridBagConstraints(1, 0, 1, 1, 10.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        line2Text.setHorizontalAlignment(SwingConstants.RIGHT);
        contentContainer.add(line2Text, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        contentContainer.add(line2TextField, new GridBagConstraints(1, 1, 1, 1, 10.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        outContainer.add(contentContainer, BorderLayout.CENTER);
        settingPanel.add(outContainer);
    }

    private void initAndAddFilterContainer() {
        Container filterSettingContainer = new Container();
        filterSettingContainer.setLayout(new BorderLayout(0, 5));

        final JLabel filterLabel = new JLabel("Filter setting");
        filterSettingContainer.add(filterLabel, BorderLayout.NORTH);

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
                AddFilterRuleDialog dialog = new AddFilterRuleDialog(settingPanel,
                        "Set your filter rule", false);
                dialog.setOnOKClickedListener(new AddFilterRuleDialog.OnOKClickedListener() {
                    @Override
                    public void onClick(FilterRule.FilterRuleType ruleType, String filterNameString) {
                        filterRules.add(new FilterRule(ruleType, filterNameString));
                        int index = filterList.getSelectedIndex();
                        filterList.setListData(getFilterRulesDisplayString());
                        filterList.setSelectedIndex(index);
                    }
                });
                dialog.show();
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
