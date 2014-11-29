package ui;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Wesley on 11/29/14.
 */
public class MultiSelectDialog extends DialogWrapper {

    public static final double GOLDEN_RATIO = 0.618;
    public static final double REVERSE_GOLDEN_RATIO = 1 - GOLDEN_RATIO;

    public interface OnOKClickedListener {
        //todo: onClick, params should be multi selected GoogleSupportedLanguages
        public void onClick();
    }

    protected String myMessage;
    protected Icon myIcon;
    private MyBorderLayout myLayout;

    private OnOKClickedListener onOKClickedListener;

    public void setOnOKClickedListener(OnOKClickedListener onOKClickedListener) {
        this.onOKClickedListener = onOKClickedListener;
    }

    public MultiSelectDialog(@Nullable Project project,
                         String message,
                         String title,
                         @Nullable Icon icon,
                         @Nullable DoNotAskOption doNotAskOption,
                         boolean canBeParent) {
        super(project, canBeParent);
        _init(title, message, icon, doNotAskOption);
    }

    public MultiSelectDialog(@Nullable Project project, String message, String title, @Nullable Icon icon,
                         boolean canBeParent) {
        super(project, canBeParent);
        _init(title, message, icon, null);
    }

    public MultiSelectDialog(@NotNull Component parent, String message, String title, @Nullable Icon icon) {
        this(parent, message, title, icon, false);
    }

    public MultiSelectDialog(@NotNull Component parent,
                         String message,
                         String title,
                         @Nullable Icon icon,
                         boolean canBeParent) {
        super(parent, canBeParent);
        _init(title, message, icon, null);
    }

    public MultiSelectDialog(String message, String title, int defaultOptionIndex, @Nullable Icon icon) {
        this(message, title, defaultOptionIndex, icon, false);
    }

    public MultiSelectDialog(String message, String title, int defaultOptionIndex, @Nullable Icon icon, boolean canBeParent) {
        super(canBeParent);
        _init(title, message, icon, null);
    }

    public MultiSelectDialog(String message, String title, @Nullable Icon icon, @Nullable DoNotAskOption doNotAskOption) {
        super(false);
        _init(title, message, icon, doNotAskOption);
    }

    public MultiSelectDialog(String message, String title, int defaultOptionIndex, Icon icon, DoNotAskOption doNotAskOption) {
        this(message, title, icon, doNotAskOption);
    }

    protected MultiSelectDialog() {
        super(false);
    }

    protected MultiSelectDialog(Project project) {
        super(project, false);
    }

    protected void _init(String title,
                         String message,
                         @Nullable Icon icon,
                         @Nullable DoNotAskOption doNotAskOption) {
        setTitle(title);
        if (Messages.isMacSheetEmulation()) {
            setUndecorated(true);
        }
        myMessage = message;
        myIcon = icon;
        if (SystemInfo.isMac) {
            setButtonsAlignment(SwingConstants.RIGHT);
        } else {
            setButtonsAlignment(SwingConstants.LEFT);
        }
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
            onOKClickedListener.onClick();
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
                final int scrollSize = (int) new JScrollBar(Adjustable.VERTICAL).getPreferredSize().getWidth();
                final Dimension preferredSize =
                        new Dimension(Math.min(textSize.width, (int)(screenSize.width * REVERSE_GOLDEN_RATIO)) + scrollSize,
                                Math.min(textSize.height, screenSize.height / 3) + scrollSize);
                pane.setPreferredSize(preferredSize);
                panel.add(pane, BorderLayout.CENTER);
            } else {
                panel.add(messageComponent, BorderLayout.CENTER);
            }
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
