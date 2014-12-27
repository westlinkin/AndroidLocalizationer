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

package ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.mac.foundation.MacUtil;
import com.intellij.util.ui.UIUtil;
import data.StorageDataKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Wesley Lin on 12/27/14.
 */
public class GoogleAlertDialog extends DialogWrapper {

    private String mMessage;

    public GoogleAlertDialog(@Nullable Component parent,
                               boolean canBeParent) {
        super(parent, canBeParent);
        _init("Information", "Google Translate API is a paid service. The pricing is based on usage. Translation usage is calculated in millions of characters (M), where 1 M = 106 characters");
    }

    protected void _init(String title, String message) {
        setTitle(title);
        mMessage = message;
        if (Messages.isMacSheetEmulation()) {
            setUndecorated(true);
        }

        setButtonsAlignment(SwingConstants.RIGHT);
        setDoNotAskOption(null);
        init();
        if (Messages.isMacSheetEmulation()) {
            MacUtil.adjustFocusTraversal(myDisposable);
        }
    }

    private Action detailAction = new AbstractAction(UIUtil.replaceMnemonicAmpersand("Pricing details")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Desktop.getDesktop().browse(new URI("https://cloud.google.com/translate/v2/pricing"));
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            close(5, true);
        }
    };

    private Action neverShowAction = new AbstractAction(UIUtil.replaceMnemonicAmpersand("Never show this again")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            PropertiesComponent.getInstance().setValue(StorageDataKey.GoogleAlertMsgShownSetting, String.valueOf(true));
            close(6, true);
        }
    };

    @NotNull
    @Override
    protected Action[] createActions() {
        Action[] actions;
        if (SystemInfo.isMac) {
            actions = new Action[]{myCancelAction, neverShowAction, detailAction, myOKAction};
        } else {
            actions = new Action[]{myOKAction, detailAction, neverShowAction, myCancelAction};
        }
        return actions;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));

        // icon
        JLabel iconLabel = new JLabel(Messages.getInformationIcon());
        Container container = new Container();
        container.setLayout(new BorderLayout());
        container.add(iconLabel, BorderLayout.NORTH);
        panel.add(container, BorderLayout.WEST);

        if (mMessage != null) {
            final JTextPane messageComponent = MultiSelectDialog.createMessageComponent(mMessage);

            final Dimension screenSize = messageComponent.getToolkit().getScreenSize();
            final Dimension textSize = messageComponent.getPreferredSize();
            if (mMessage.length() > 100) {
                final JScrollPane pane = ScrollPaneFactory.createScrollPane(messageComponent);
                pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                final int scrollSize = (int)new JScrollBar(Adjustable.VERTICAL).getPreferredSize().getWidth();
                final Dimension preferredSize =
                        new Dimension(Math.min(textSize.width, screenSize.width / 2) + scrollSize,
                                Math.min(textSize.height, screenSize.height / 3) + scrollSize);
                pane.setPreferredSize(preferredSize);
                panel.add(pane, BorderLayout.CENTER);
            }
            else {
                panel.add(messageComponent, BorderLayout.CENTER);
            }
        }
        return panel;
    }
}
