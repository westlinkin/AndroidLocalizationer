package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import module.AndroidString;
import ui.MultiSelectDialog;

import java.io.IOException;
import java.util.List;

/**
 * Created by Wesley on 11/26/14.
 */
public class ConvertToOtherLanguages extends AnAction implements MultiSelectDialog.OnOKClickedListener{
    private static final String LOCALIZATION_TITLE = "Choose alternative string resources";
    private static final String LOCALIZATION_MSG = "Warning: " +
            "The string resources are translated by Google Translation API, " +
            "try keeping your string resources simple, so that the result is more satisfied.";

    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        VirtualFile clickedFile = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
        if (clickedFile.getExtension() == null || !clickedFile.getExtension().equals("xml")) {
            showErrorDialog(project, "Target file is not Android resource.");
            return;
        }

        List<AndroidString> androidStrings = null;
        try {
             androidStrings = AndroidString.getAndroidStringsList(clickedFile.contentsToByteArray());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (androidStrings == null || androidStrings.isEmpty()) {
            showErrorDialog(project, "Target file does not contain any strings.");
            return;
        }

        for (AndroidString androidString : androidStrings)
            System.out.println(androidString);


        // show dialog
        MultiSelectDialog multiSelectDialog = new MultiSelectDialog(project, LOCALIZATION_MSG,
                LOCALIZATION_TITLE, null, false);
        multiSelectDialog.setOnOKClickedListener(this);
        multiSelectDialog.show();

    }

    @Override
    public void onClick() {
        //todo: handle multi selected result
        System.out.println("onClick");
    }

    private void showErrorDialog(Project project, String msg) {
        Messages.showErrorDialog(project, msg, "Error");
    }
}
