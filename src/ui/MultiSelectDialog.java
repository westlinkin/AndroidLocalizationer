/*
 * Copyright [2014] [Wesley Lin]
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
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.mac.foundation.MacUtil;
import com.intellij.util.Alarm;
import com.intellij.util.ui.UIUtil;
import data.StorageDataKey;
import module.SupportedLanguages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Wesley Lin on 11/29/14.
 */
public class MultiSelectDialog extends DialogWrapper {

    public static final double GOLDEN_RATIO = 0.618;
    public static final double REVERSE_GOLDEN_RATIO = 1 - GOLDEN_RATIO;

    public interface OnOKClickedListener {
        public void onClick(List<SupportedLanguages> selectedLanguages, boolean overrideChecked);
    }

    private PropertiesComponent propertiesComponent;
    protected String myMessage;
    protected Icon myIcon;
    private MyBorderLayout myLayout;

    private JCheckBox myCheckBox;
    private String myCheckboxText;
    private boolean myChecked;

    private java.util.List<SupportedLanguages> data = SupportedLanguages.getAllSupportedLanguages();
    private java.util.List<SupportedLanguages> selectedLanguages = new ArrayList<SupportedLanguages>();
    private OnOKClickedListener onOKClickedListener;

    public void setOnOKClickedListener(OnOKClickedListener onOKClickedListener) {
        this.onOKClickedListener = onOKClickedListener;
    }

    public MultiSelectDialog(@Nullable Project project,
                             String message,
                             String title,
                             @Nullable Icon icon,
                             @Nullable String checkboxText,
                             boolean checkboxStatus,
                             boolean canBeParent) {
        super(project, canBeParent);
        _init(project, title, message, icon, checkboxText, checkboxStatus, null);
    }

    protected void _init(Project project,
                         String title,
                         String message,
                         @Nullable Icon icon,
                         @Nullable String checkboxText,
                         boolean checkboxStatus,
                         @Nullable DoNotAskOption doNotAskOption) {
        setTitle(title);
        if (Messages.isMacSheetEmulation()) {
            setUndecorated(true);
        }
        propertiesComponent = PropertiesComponent.getInstance(project);
        myMessage = message;
        myIcon = icon;
        myCheckboxText = checkboxText;
        myChecked = checkboxStatus;
        setButtonsAlignment(SwingConstants.RIGHT);
        setDoNotAskOption(doNotAskOption);
        init();
        if (Messages.isMacSheetEmulation()) {
            MacUtil.adjustFocusTraversal(myDisposable);
        }
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (onOKClickedListener != null) {
            onOKClickedListener.onClick(selectedLanguages, myCheckBox.isSelected());
        }
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action[] actions;
        if (SystemInfo.isMac) {
            actions = new Action[]{myCancelAction, myOKAction};
        } else {
            actions = new Action[]{myOKAction, myCancelAction};
        }
        return actions;
    }

    @Override
    public void doCancelAction() {
        close(-1);
    }

    @Override
    protected JComponent createCenterPanel() {
        return doCreateCenterPanel();
    }

    @NotNull
    LayoutManager createRootLayout() {
        return Messages.isMacSheetEmulation() ? myLayout = new MyBorderLayout() : new BorderLayout();
    }

    @Override
    protected void dispose() {
        if (Messages.isMacSheetEmulation()) {
            animate();
        } else {
            super.dispose();
        }
    }

    @Override
    public void show() {
        if (Messages.isMacSheetEmulation()) {
            setInitialLocationCallback(new Computable<Point>() {
                @Override
                public Point compute() {
                    JRootPane rootPane = SwingUtilities.getRootPane(getWindow().getParent());
                    if (rootPane == null) {
                        rootPane = SwingUtilities.getRootPane(getWindow().getOwner());
                    }

                    Point p = rootPane.getLocationOnScreen();
                    p.x += (rootPane.getWidth() - getWindow().getWidth()) / 2;
                    return p;
                }
            });
            animate();
            if (SystemInfo.isJavaVersionAtLeast("1.7")) {
                try {
                    Method method = Class.forName("java.awt.Window").getDeclaredMethod("setOpacity", float.class);
                    if (method != null) method.invoke(getPeer().getWindow(), .8f);
                } catch (Exception exception) {
                }
            }
            setAutoAdjustable(false);
            setSize(getPreferredSize().width, 0);//initial state before animation, zero height
        }
        super.show();
    }

    private void animate() {
        final int height = getPreferredSize().height;
        final int frameCount = 10;
        final boolean toClose = isShowing();


        final AtomicInteger i = new AtomicInteger(-1);
        final Alarm animator = new Alarm(myDisposable);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int state = i.addAndGet(1);

                double linearProgress = (double) state / frameCount;
                if (toClose) {
                    linearProgress = 1 - linearProgress;
                }
                myLayout.myPhase = (1 - Math.cos(Math.PI * linearProgress)) / 2;
                Window window = getPeer().getWindow();
                Rectangle bounds = window.getBounds();
                bounds.height = (int) (height * myLayout.myPhase);

                window.setBounds(bounds);

                if (state == 0 && !toClose && window.getOwner() instanceof IdeFrame) {
                    WindowManager.getInstance().requestUserAttention((IdeFrame) window.getOwner(), true);
                }

                if (state < frameCount) {
                    animator.addRequest(this, 10);
                } else if (toClose) {
                    MultiSelectDialog.super.dispose();
                }
            }
        };
        animator.addRequest(runnable, 10, ModalityState.stateForComponent(getRootPane()));
    }

    protected JComponent doCreateCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        if (myIcon != null) {
            JLabel iconLabel = new JLabel(myIcon);
            Container container = new Container();
            container.setLayout(new BorderLayout());
            container.add(iconLabel, BorderLayout.NORTH);
            panel.add(container, BorderLayout.WEST);
        }
        if (myMessage != null) {
            final JTextPane messageComponent = createMessageComponent(myMessage);

            final Dimension screenSize = messageComponent.getToolkit().getScreenSize();
            final Dimension textSize = messageComponent.getPreferredSize();
            if (myMessage.length() > 100) {
                final JScrollPane pane = ScrollPaneFactory.createScrollPane(messageComponent);
                pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                final int scrollSize = (int) new JScrollBar(Adjustable.VERTICAL).getPreferredSize().getWidth() + 12;
                final Dimension preferredSize =
                        new Dimension(Math.min(textSize.width, (int)(screenSize.width * REVERSE_GOLDEN_RATIO)) + scrollSize,
                                Math.min(textSize.height, screenSize.height / 3) + scrollSize);
                pane.setPreferredSize(preferredSize);
                panel.add(pane, BorderLayout.NORTH);
            } else {
                panel.add(messageComponent, BorderLayout.NORTH);
            }
        }

        if (!data.isEmpty()) {
            Container container = new Container();
            int gridCol = 2;
            int gridRow = (data.size() % gridCol == 0) ? data.size() / gridCol : data.size() / gridCol + 1;
            container.setLayout(new GridLayout(gridRow, gridCol));
            for (final SupportedLanguages language : data) {
                JCheckBox checkbox = new JCheckBox(language.name() + " (" + language.getLanguageDisplayName() + ") ");
                checkbox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            if (!selectedLanguages.contains(language)) {
                                selectedLanguages.add(language);
                            }
                        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                            if (selectedLanguages.contains(language)) {
                                selectedLanguages.remove(language);
                            }
                        }
                    }
                });
                checkbox.setSelected(
                        propertiesComponent.getBoolean(StorageDataKey.SupportedLanguageCheckStatusPrefix + language.getLanguageCode(), false));
                container.add(checkbox);
            }
            panel.add(container, BorderLayout.CENTER);
        }

        if (myCheckboxText != null) {

            myCheckBox = new JCheckBox(myCheckboxText);
            myCheckBox.setSelected(myChecked);
            myCheckBox.setMargin(new Insets(2, -4, 0, 0));

            panel.add(myCheckBox, BorderLayout.SOUTH);
        }

        return panel;
    }

    protected static JTextPane createMessageComponent(final String message) {
        final JTextPane messageComponent = new JTextPane();
        return configureMessagePaneUi(messageComponent, message);
    }

    @Override
    protected void doHelpAction() {
        // do nothing
    }

    @NotNull
    public static JTextPane configureMessagePaneUi(JTextPane messageComponent, String message) {
        return configureMessagePaneUi(messageComponent, message, true);
    }

    @NotNull
    public static JTextPane configureMessagePaneUi(JTextPane messageComponent,
                                                   String message,
                                                   final boolean addBrowserHyperlinkListener) {
        messageComponent.setFont(UIUtil.getLabelFont());
        if (BasicHTML.isHTMLString(message)) {
            final HTMLEditorKit editorKit = new HTMLEditorKit();
            editorKit.getStyleSheet().addRule(UIUtil.displayPropertiesToCSS(UIUtil.getLabelFont(), UIUtil.getLabelForeground()));
            messageComponent.setEditorKit(editorKit);
            messageComponent.setContentType(UIUtil.HTML_MIME);
            if (addBrowserHyperlinkListener) {
                messageComponent.addHyperlinkListener(BrowserHyperlinkListener.INSTANCE);
            }
        }
        messageComponent.setText(message);
        messageComponent.setEditable(false);
        if (messageComponent.getCaret() != null) {
            messageComponent.setCaretPosition(0);
        }

        if (UIUtil.isUnderNimbusLookAndFeel()) {
            messageComponent.setOpaque(false);
            messageComponent.setBackground(UIUtil.TRANSPARENT_COLOR);
        }
        else {
            messageComponent.setBackground(UIUtil.getOptionPaneBackground());
        }

        messageComponent.setForeground(UIUtil.getLabelForeground());
        return messageComponent;
    }

    private static class MyBorderLayout extends BorderLayout {
        private double myPhase = 0;//it varies from 0 (hidden state) to 1 (fully visible)

        private MyBorderLayout() {
        }

        @Override
        public void layoutContainer(Container target) {
            final Dimension realSize = target.getSize();
            target.setSize(target.getPreferredSize());

            super.layoutContainer(target);

            target.setSize(realSize);

            synchronized (target.getTreeLock()) {
                int yShift = (int)((1 - myPhase) * target.getPreferredSize().height);
                Component[] components = target.getComponents();
                for (Component component : components) {
                    Point point = component.getLocation();
                    point.y -= yShift;
                    component.setLocation(point);
                }
            }
        }
    }
}
