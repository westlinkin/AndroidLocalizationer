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

package data.task;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.vfs.VcsVirtualFile;
import com.intellij.openapi.vfs.VirtualFile;
import data.Key;
import data.Log;
import language_engine.TranslationEngineType;
import language_engine.bing.BingTranslationApi;
import module.AndroidString;
import module.SupportedLanguages;
import org.jetbrains.annotations.NotNull;

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

            Log.i(translationResult.toString());

            // todo: write to file
            String fileName = getValueResourcePath(language);
            Log.i("fileName: " + fileName);


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
        return  translatedAndroidStrings;
    }

    private List<AndroidString> filterAndroidString(List<AndroidString> origin,
                                                           SupportedLanguages language,
                                                           boolean override) {
        List<AndroidString> result = new ArrayList<AndroidString>();

        for (AndroidString androidString : origin) {
            // filter NAL_
            if (androidString.getKey().startsWith(Key.NO_NEED_TRANSLATION_ANDROID_STRING_PREFIX))
                continue;

            // override
            if (!override) {
                String virturalFilePath = getValueResourcePath(language);
                // check if there is the file
//                getValueFolderName(language) + clickedFileName;

                // check if there is the androidString in this file
                // if there is, filter it

            }

            result.add(androidString);
        }

        return result;
    }

}
