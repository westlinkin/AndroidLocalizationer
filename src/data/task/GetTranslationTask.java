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

package data.task;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import data.Key;
import data.Log;
import language_engine.TranslationEngineType;
import language_engine.bing.BingTranslationApi;
import module.AndroidString;
import module.SupportedLanguages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley Lin on 12/1/14.
 */
public class GetTranslationTask extends Task.Backgroundable{

    private List<SupportedLanguages> selectedLanguages;
    private List<AndroidString> androidStrings;
    private double indicatorFractionFrame;
    private TranslationEngineType translationEngineType;
    private boolean override;
    private VirtualFile clickedFile;

    public GetTranslationTask(Project project, String title,
                              List<SupportedLanguages> selectedLanguages,
                              List<AndroidString> androidStrings,
                              TranslationEngineType translationEngineType,
                              boolean override,
                              VirtualFile clickedFile) {
        super(project, title);
        this.selectedLanguages = selectedLanguages;
        this.androidStrings = androidStrings;
        this.translationEngineType = translationEngineType;
        this.indicatorFractionFrame = 1.0d / (double)(this.selectedLanguages.size());
        this.override = override;
        this.clickedFile = clickedFile;
    }

    @Override
    public void run(ProgressIndicator indicator) {
        for (int i = 0; i < selectedLanguages.size(); i++) {
            SupportedLanguages language = selectedLanguages.get(i);
            List<AndroidString> translationResult = getTranslationEngineResult(
                    filterAndroidString(androidStrings, language, override),
                    language,
                    SupportedLanguages.English,
                    translationEngineType
            );
            indicator.setFraction(indicatorFractionFrame * (double)(i));
            indicator.setText("Translating to " + language.getLanguageEnglishDisplayName()
                    + " (" + language.getLanguageDisplayName() + ")");

            if (translationResult == null) {
                // todo: wrong with result, possibly api quota is out, should show error msg
                return;
            }
            String fileName = getValueResourcePath(language);
            List<AndroidString> fileContent = getTargetAndroidStrings(androidStrings, translationResult, fileName, override);

            writeAndroidStringToLocal(myProject, fileName, fileContent);
        }
    }

    private String getValueResourcePath(SupportedLanguages language) {
        String resPath = clickedFile.getPath().substring(0,
                clickedFile.getPath().indexOf("/res/") + "/res/".length());

        return resPath + "values-" + language.getAndroidStringFolderNameSuffix()
                + "/" + clickedFile.getName();
    }

    private List<AndroidString> getTranslationEngineResult(@NotNull List<AndroidString> needToTranslatedString,
                                                           @NotNull SupportedLanguages targetLanguageCode,
                                                           @NotNull SupportedLanguages sourceLanguageCode,
                                                           TranslationEngineType translationEngineType) {

        List<String> querys = AndroidString.getAndroidStringValues(needToTranslatedString);
        List<String> result = null;

        switch (translationEngineType) {
            case Bing:
                String accessToken = BingTranslationApi.getAccessToken();
                result = BingTranslationApi.getTranslatedStringArrays(accessToken, querys, sourceLanguageCode, targetLanguageCode);
                // return XXX
                break;
            case Google:
                // todo
                break;
        }

        if (result == null)
            return null;
        List<AndroidString> translatedAndroidStrings = new ArrayList<AndroidString>();

        for (int i = 0; i < needToTranslatedString.size(); i++) {
            translatedAndroidStrings.add(new AndroidString(
                    needToTranslatedString.get(i).getKey(), result.get(i)));
        }
        return translatedAndroidStrings;
    }

    private List<AndroidString> filterAndroidString(List<AndroidString> origin,
                                                           SupportedLanguages language,
                                                           boolean override) {
        List<AndroidString> result = new ArrayList<AndroidString>();


        VirtualFile targetStringFile = LocalFileSystem.getInstance().findFileByPath(
                getValueResourcePath(language));
        List<AndroidString> targetAndroidStrings = new ArrayList<AndroidString>();
        if (targetStringFile != null) {
            try {
                targetAndroidStrings = AndroidString.getAndroidStringsList(targetStringFile.contentsToByteArray());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

//        Log.i("targetAndroidString: " + targetAndroidStrings.toString());
        for (AndroidString androidString : origin) {
            // filter NAL_
            if (androidString.getKey().startsWith(Key.NO_NEED_TRANSLATION_ANDROID_STRING_PREFIX))
                continue;

            // override
            if (!override && !targetAndroidStrings.isEmpty()) {
                // check if there is the androidString in this file
                // if there is, filter it
                if (isAndroidStringListContainsKey(targetAndroidStrings, androidString.getKey())) {
                    continue;
                }
            }

            result.add(androidString);
        }

        return result;
    }

    private static List<AndroidString> getTargetAndroidStrings(List<AndroidString> sourceAndroidStrings,
                                                      List<AndroidString> translatedAndroidStrings,
                                                      String fileName,
                                                      boolean override) {
        VirtualFile existenceFile = LocalFileSystem.getInstance().findFileByPath(fileName);
        List<AndroidString> existenceAndroidStrings = null;
        if (existenceFile != null && !override) {
            try {
                existenceAndroidStrings = AndroidString.getAndroidStringsList(existenceFile.contentsToByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            existenceAndroidStrings = new ArrayList<AndroidString>();
        }

        Log.i("sourceAndroidStrings: " + sourceAndroidStrings,
                "translatedAndroidStrings: " + translatedAndroidStrings,
                "existenceAndroidStrings: " + existenceAndroidStrings);

        List<AndroidString> targetAndroidStrings = new ArrayList<AndroidString>(sourceAndroidStrings);

        for(AndroidString androidString : targetAndroidStrings) {
            // if override is checked, skip setting the existence value, for performance issue
            if (!override) {
                String existenceValue = getAndroidStringValueInList(existenceAndroidStrings, androidString.getKey());
                if (existenceValue != null) {
                    androidString.setValue(existenceValue);
                }
            }

            String translatedValue = getAndroidStringValueInList(translatedAndroidStrings, androidString.getKey());
            if (translatedValue != null) {
                androidString.setValue(translatedValue);
            }
        }

        Log.i("targetAndroidStrings: " + targetAndroidStrings);
        return targetAndroidStrings;
    }

    private static void writeAndroidStringToLocal(final Project myProject, String filePath, List<AndroidString> fileContent) {
        File file = new File(filePath);
        final VirtualFile virtualFile;
        boolean fileExits = true;
        try {
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                fileExits = false;
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(getFileContent(fileContent));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileExits) {
            virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
            if (virtualFile == null)
                return;
            virtualFile.refresh(true, false, new Runnable() {
                @Override
                public void run() {
                    openFileInEditor(myProject, virtualFile);
                }
            });
        } else {
            virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
            openFileInEditor(myProject, virtualFile);
        }
    }

    private static void openFileInEditor(Project myProject, @Nullable final VirtualFile file) {
        if (file == null)
            return;

        final FileEditorManager editorManager = FileEditorManager.getInstance(myProject);
        editorManager.openFile(file, true);
    }

    private static String getFileContent(List<AndroidString> fileContent) {
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
        String stringResourceHeader = "<resources>\n\n";
        String stringResourceTail = "</resources>\n";

        StringBuilder sb = new StringBuilder();
        sb.append(xmlHeader).append(stringResourceHeader);
        for (AndroidString androidString : fileContent) {
            sb.append("\t").append(androidString.toString()).append("\n");
        }
        sb.append("\n").append(stringResourceTail);
        return sb.toString();
    }

    private static boolean isAndroidStringListContainsKey(List<AndroidString> androidStrings, String key) {
        List<String> keys = AndroidString.getAndroidStringKeys(androidStrings);
        return keys.contains(key);
    }

    public static String getAndroidStringValueInList(List<AndroidString> androidStrings, String key) {
        for (AndroidString androidString : androidStrings) {
            if (androidString.getKey().equals(key)) {
                return androidString.getValue();
            }
        }
        return null;
    }

}
