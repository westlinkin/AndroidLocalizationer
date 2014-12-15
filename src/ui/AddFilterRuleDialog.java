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

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.mac.foundation.MacUtil;
import com.intellij.util.Alarm;
import module.FilterRule;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Wesley Lin on 12/15/14.
 */
public class AddFilterRuleDialog extends DialogWrapper {

    public interface OnOKClickedListener {
        public void onClick(FilterRule.FilterRuleType ruleType, String filterNameString);
    }

    private MyBorderLayout myLayout;
    private ComboBox ruleType;
    private JTextField filterName;

    private OnOKClickedListener onOKClickedListener;

    public void setOnOKClickedListener(OnOKClickedListener onOKClickedListener) {
        this.onOKClickedListener = onOKClickedListener;
    }

    public AddFilterRuleDialog(@Nullable Component parent,
                               String title,
                               boolean canBeParent) {
        super(parent, canBeParent);
        _init(title, null);
    }

    protected void _init(String title,
                         @Nullable DoNotAskOption doNotAskOption) {
        setTitle(title);
        if (Messages.isMacSheetEmulation()) {
            setUndecorated(true);
        }

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
        if (onOKClickedListener != null && ruleType != null && filterName != null) {
            onOKClickedListener.onClick((FilterRule.FilterRuleType) ruleType.getSelectedItem(),
                    filterName.getText());
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
                    AddFilterRuleDialog.super.dispose();
                }
            }
        };
        animator.addRequest(runnable, 10, ModalityState.stateForComponent(getRootPane()));
    }

    protected JComponent doCreateCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));

        FilterRule.FilterRuleType[] types = FilterRule.FilterRuleType.values();

        ruleType = new ComboBox(types);
        ruleType.setEnabled(true);
        ruleType.setSelectedIndex(0);

        panel.add(ruleType, BorderLayout.WEST);

        filterName = new JTextField(20);
        PromptSupport.setPrompt("Set the string name here", filterName);
        panel.add(filterName, BorderLayout.CENTER);

        return panel;
    }

    @Override
    protected void doHelpAction() {
        // do nothing
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
