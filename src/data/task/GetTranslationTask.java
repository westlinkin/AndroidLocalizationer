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
import data.Log;
import language_engine.TranslationEngineType;
import language_engine.bing.BingTranslationApi;
import module.AndroidString;
import module.SupportedLanguages;
import org.jetbrains.annotations.NotNull;

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

    public GetTranslationTask(Project project, String title,
                              List<SupportedLanguages> selectedLanguages,
                              List<AndroidString> androidStrings,
                              TranslationEngineType translationEngineType,
                              boolean override) {
        super(project, title);
        this.selectedLanguages = selectedLanguages;
        this.androidStrings = androidStrings;
        this.translationEngineType = translationEngineType;
        this.indicatorFractionFrame = 1.0d / (double)(this.selectedLanguages.size());
        this.override = override;
    }

    @Override
    public void run(ProgressIndicator indicator) {
        for (int i = 0; i < selectedLanguages.size(); i++) {
            SupportedLanguages language = selectedLanguages.get(i);
            List<AndroidString> translationResult = getTranslationEngineResult(
                    // todo: need to filter the androidString
                    AndroidString.getAndroidStringValues(androidStrings),
                    language,
                    SupportedLanguages.English,
                    translationEngineType
            );
            indicator.setFraction(indicatorFractionFrame * (double)(i));
            indicator.setText("Translating to " + language.getLanguageEnglishDisplayName()
                    + " (" + language.getLanguageDisplayName() + ")");

            // todo: write to file

        }
    }

    private List<AndroidString> getTranslationEngineResult(@NotNull List<String> querys,
                                                           @NotNull SupportedLanguages targetLanguageCode,
                                                           @NotNull SupportedLanguages sourceLanguageCode,
                                                           TranslationEngineType translationEngineType) {
        // todo
        switch (translationEngineType) {
            case Bing:
                String accessToken = BingTranslationApi.getAccessToken();
                Log.i("accessToken: " + accessToken);
                BingTranslationApi.getTranslatedStringArrays(accessToken, querys, sourceLanguageCode, targetLanguageCode);
                // return XXX
                break;
            case Google:
                break;
        }
        return  null;
    }

}
