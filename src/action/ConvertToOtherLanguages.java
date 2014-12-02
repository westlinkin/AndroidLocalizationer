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

package action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import data.StorageDataKey;
import data.task.GetTranslationTask;
import module.AndroidString;
import module.SupportedLanguages;
import ui.MultiSelectDialog;

import java.io.IOException;
import java.util.List;

/**
 * Created by Wesley Lin on 11/26/14.
 */
public class ConvertToOtherLanguages extends AnAction implements MultiSelectDialog.OnOKClickedListener {

    private static final String LOCALIZATION_TITLE = "Choose alternative string resources";
    private static final String LOCALIZATION_MSG = "Warning: " +
            "The string resources are translated by Google Translation API, " +
            "try keeping your string resources simple, so that the result is more satisfied.";
    private static final String OVERRIDE_EXITS_STRINGS = "Override the exiting strings";

    private Project project;
    private List<AndroidString> androidStringsInStringFile = null;

    public void actionPerformed(AnActionEvent e) {

        project = e.getProject();
        if (project == null) {
            return;
        }

        VirtualFile clickedFile = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
        if (clickedFile.getExtension() == null || !clickedFile.getExtension().equals("xml")) {
            showErrorDialog(project, "Target file is not an Android string resource.");
            return;
        }


        try {
            androidStringsInStringFile = AndroidString.getAndroidStringsList(clickedFile.contentsToByteArray());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (androidStringsInStringFile == null || androidStringsInStringFile.isEmpty()) {
            showErrorDialog(project, "Target file does not contain any strings.");
            return;
        }

        // show dialog
        MultiSelectDialog multiSelectDialog = new MultiSelectDialog(project,
                LOCALIZATION_MSG,
                LOCALIZATION_TITLE,
                null,
                OVERRIDE_EXITS_STRINGS,
                PropertiesComponent.getInstance(project).getBoolean(StorageDataKey.OverrideCheckBoxStatus, false),
                false);
        multiSelectDialog.setOnOKClickedListener(this);
        multiSelectDialog.show();
    }

    @Override
    public void onClick(List<SupportedLanguages> selectedLanguages, boolean overrideChecked) {
        // set consistence data
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        propertiesComponent.setValue(StorageDataKey.OverrideCheckBoxStatus, String.valueOf(overrideChecked));

        List<SupportedLanguages> allData = SupportedLanguages.getAllSupportedLanguages();

        for (SupportedLanguages language : allData) {
            propertiesComponent.setValue(StorageDataKey.SupportedLanguageCheckStatusPrefix + language.getLanguageCode(),
                    String.valueOf(selectedLanguages.contains(language)));
        }

        // todo: handle multi selected result
        // todo: title should adding using which language engine
        new GetTranslationTask(project, "Translation in progress", selectedLanguages, androidStringsInStringFile)
                .queue();
    }

    private void showErrorDialog(Project project, String msg) {
        Messages.showErrorDialog(project, msg, "Error");
    }
}
