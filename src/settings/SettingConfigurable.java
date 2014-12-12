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
import data.Log;
import data.StorageDataKey;
import language_engine.TranslationEngineType;
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

    private boolean languageEngineChanged = false;

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
            settingPanel = new JPanel(new BorderLayout());

            // header UI
            Container container = new Container();
            container.setLayout(new BorderLayout());

            // todo: only one language engine for now, will add a few more in the future
            currentEngine = TranslationEngineType.Bing;
            TranslationEngineType[] items = TranslationEngineType.getLanguageEngineArray();
            languageEngineBox = new JComboBox(items);
            languageEngineBox.setEnabled(true);
            languageEngineBox.setSelectedIndex(0);
            languageEngineBox.addActionListener(this);

            container.add(new JLabel("Language engine: "), BorderLayout.WEST);
            container.add(languageEngineBox, BorderLayout.CENTER);

            settingPanel.add(container, BorderLayout.PAGE_START);

            // todo: at first, only bing
            initBingContainer();
            settingPanel.add(bingContainer, BorderLayout.CENTER);
        }
        return settingPanel;
    }

    @Override
    public boolean isModified() {
        if (languageEngineChanged)
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
        if (languageEngineBox == null)
            return;

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

        languageEngineChanged = false;
        //todo: set currentLanguageEngine

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

    }

    @Override
    public void reset() {
        if (settingPanel == null || currentEngine == null || languageEngineBox == null)
            return;

        //todo: reset languageEngineBox
        languageEngineChanged = false;

        Log.i("reset, current engine: " + currentEngine);

        switch (currentEngine) {
            case Bing: {
                if (bingClientIdField == null || bingClientSecretField == null)
                    return;

                PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
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
        bingContainer.setLayout(new BorderLayout());

        String howto = "<html><br><a href=\"http://blogs.msdn.com/b/translation/p/gettingstarted1.aspx\">How to set ClientId and ClientSecret?</a></html>";
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
        ((GridBagLayout)contentContainer.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentContainer.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
        ((GridBagLayout)contentContainer.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

        contentContainer.add(new JLabel("<html><br></html>"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        JLabel clientIdLabel = new JLabel("Client Id:");
        clientIdLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        contentContainer.add(clientIdLabel, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        contentContainer.add(bingClientIdField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        JLabel clientSecretLabel = new JLabel("Client Secret:");
        clientSecretLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        contentContainer.add(clientSecretLabel, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        contentContainer.add(bingClientSecretField, new GridBagConstraints(1, 2, 1, 1, 10.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        bingContainer.add(contentContainer, BorderLayout.CENTER);
    }
}
