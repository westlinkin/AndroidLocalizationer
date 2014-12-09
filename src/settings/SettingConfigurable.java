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

            settingPanel.add(container, BorderLayout.NORTH);

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

        bingContainer = new Container();
        bingContainer.setLayout(new BorderLayout());

        Container container = new Container();
        container.setLayout(new BorderLayout());

        String howto = "<html><br><br><a href=\"http://blogs.msdn.com/b/translation/p/gettingstarted1.aspx\">How to set ClientId and ClientSecret?</a></html>";
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

        Container clientIdContainer = new Container();
        clientIdContainer.setLayout(new BorderLayout());
        clientIdContainer.add(new JLabel("Client Id: "), BorderLayout.WEST);
        //todo: add textbox

        container.add(howtoLabel, BorderLayout.NORTH);

        bingContainer.add(container, BorderLayout.NORTH);


    }
}
