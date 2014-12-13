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

package action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import data.Log;
import data.StorageDataKey;
import data.task.GetTranslationTask;
import language_engine.TranslationEngineType;
import module.AndroidString;
import module.SupportedLanguages;
import org.jetbrains.annotations.Nullable;
import ui.MultiSelectDialog;

import java.io.IOException;
import java.util.List;

/**
 * Created by Wesley Lin on 11/26/14.
 */
public class ConvertToOtherLanguages extends AnAction implements MultiSelectDialog.OnOKClickedListener {

    private static final String LOCALIZATION_TITLE = "Choose alternative string resources";
    private static final String LOCALIZATION_MSG = "Warning: " +
            "The string resources are translated by %s, " +
            "try keeping your string resources simple, so that the result is more satisfied.";
    private static final String OVERRIDE_EXITS_STRINGS = "Override the existing strings";

    private Project project;
    private List<AndroidString> androidStringsInStringFile = null;

    public TranslationEngineType defaultTranslationEngine = TranslationEngineType.Bing;

    private VirtualFile clickedFile;

    public ConvertToOtherLanguages() {
        super("Convert to other languages", null, IconLoader.getIcon("/icons/globe.png"));
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        boolean isStringXML = isStringXML(file);
        e.getPresentation().setEnabled(isStringXML);
        e.getPresentation().setVisible(isStringXML);
    }

    public void actionPerformed(AnActionEvent e) {
        project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null) {
            return;
        }

        clickedFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        Log.i("clicked file: " + clickedFile.getPath());

        // todo: read settings, write @defaultTranslationEngine

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
                String.format(LOCALIZATION_MSG, defaultTranslationEngine.getDisplayName()),
                LOCALIZATION_TITLE,
                null,
                OVERRIDE_EXITS_STRINGS,
                PropertiesComponent.getInstance(project).getBoolean(StorageDataKey.OverrideCheckBoxStatus, false),
                defaultTranslationEngine,
                false);
        multiSelectDialog.setOnOKClickedListener(this);
        multiSelectDialog.show();
    }

    @Override
    public void onClick(List<SupportedLanguages> selectedLanguages, boolean overrideChecked) {
        // set consistence data
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        propertiesComponent.setValue(StorageDataKey.OverrideCheckBoxStatus, String.valueOf(overrideChecked));

        List<SupportedLanguages> allData = SupportedLanguages.getAllSupportedLanguages(defaultTranslationEngine);

        for (SupportedLanguages language : allData) {
            propertiesComponent.setValue(StorageDataKey.SupportedLanguageCheckStatusPrefix + language.getLanguageCode(),
                    String.valueOf(selectedLanguages.contains(language)));
        }

        new GetTranslationTask(project, "Translation in progress, using " + defaultTranslationEngine.getDisplayName(),
                selectedLanguages, androidStringsInStringFile, defaultTranslationEngine, overrideChecked, clickedFile)
                .setCancelText("Translation has been canceled").queue();
    }

    public static void showErrorDialog(Project project, String msg) {
        Messages.showErrorDialog(project, msg, "Error");
    }

    private static boolean isStringXML(@Nullable VirtualFile file) {
        if (file == null)
            return false;

        if (!file.getName().equals("strings.xml"))
            return false;

        if (file.getParent() == null)
            return false;

        // only show popup menu for English strings
        if (!file.getParent().getName().equals("values") && !file.getParent().getName().startsWith("values-en"))
            return false;

        return true;
    }
}
