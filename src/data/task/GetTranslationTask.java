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
import language_engine.google.GoogleTranslationApi;
import module.AndroidString;
import module.SupportedLanguages;
import language_engine.google.GoogleTranslationJSON;

import java.util.List;

/**
 * Created by Wesley Lin on 12/1/14.
 */
public class GetTranslationTask extends Task.Backgroundable{

    private List<SupportedLanguages> selectedLanguages;
    private List<AndroidString> androidStrings;
    private double indicatorFractionFrame;

    public GetTranslationTask(Project project, String title,
                              List<SupportedLanguages> selectedLanguages, List<AndroidString> androidStrings) {
        super(project, title);
        this.selectedLanguages = selectedLanguages;
        this.androidStrings = androidStrings;
        this.indicatorFractionFrame = 1.0d / (double)(this.selectedLanguages.size());
    }

    @Override
    public void run(ProgressIndicator indicator) {
        // todo: get choosed language engine

        for (int i = 0; i < selectedLanguages.size(); i++) {
            SupportedLanguages language = selectedLanguages.get(i);
            GoogleTranslationJSON json = GoogleTranslationApi.getTranslationJSON(AndroidString.getAndroidStringValues(androidStrings),
                    language, SupportedLanguages.English);
            indicator.setFraction(indicatorFractionFrame * (double)(i));
            indicator.setText("Translating to " + language.name() + " (" + language.getLanguageDisplayName() + ")");

            // todo: write to file
        }
    }


}
