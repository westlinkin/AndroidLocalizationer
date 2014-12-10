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

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import data.Log;
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

    private JPanel settingPanel;

    private Container bingContainer;
    private JTextField bingClientIdField;
    private JTextField bingClientSecretField;

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

            // todo: only one language engine for now, will add a few more in the furture
            TranslationEngineType[] items = TranslationEngineType.getLanguageEngineArray();
            JComboBox comboBox = new JComboBox(items);
            comboBox.setEnabled(true);
            comboBox.setSelectedIndex(0);
            comboBox.addActionListener(this);

            container.add(new JLabel("Language engine: "), BorderLayout.WEST);
            container.add(comboBox, BorderLayout.CENTER);

            settingPanel.add(container, BorderLayout.PAGE_START);

            // todo: at first, only bing
            initBingContainer();
            settingPanel.add(bingContainer, BorderLayout.CENTER);
        }
        return settingPanel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox comboBox = (JComboBox) e.getSource();
        TranslationEngineType type = (TranslationEngineType) comboBox.getSelectedItem();
        Log.i("selected type: " + type.name());
        //todo: change other JComponents

    }

    private void initBingContainer() {
        if (bingContainer != null)
            return;

        bingClientIdField = new JTextField();
        bingClientSecretField = new JTextField();

        PromptSupport.setPrompt("Default client id", bingClientIdField);
        PromptSupport.setPrompt("Default client secret", bingClientSecretField);

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
