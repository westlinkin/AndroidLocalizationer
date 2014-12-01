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
import data.GoogleTranslationApi;
import module.AndroidString;
import module.GoogleSupportedLanguages;
import module.GoogleTranslationJSON;

import java.util.List;

/**
 * Created by Wesley Lin on 12/1/14.
 */
public class GetTranslationTask extends Task.Backgroundable{

    private List<GoogleSupportedLanguages> selectedLanguages;
    private List<AndroidString> androidStrings;
    private double indicatorFractionFrame;

    public GetTranslationTask(Project project, String title,
                              List<GoogleSupportedLanguages> selectedLanguages, List<AndroidString> androidStrings) {
        super(project, title);
        this.selectedLanguages = selectedLanguages;
        this.androidStrings = androidStrings;
        this.indicatorFractionFrame = 1.0d / (double)(this.selectedLanguages.size());
    }

    @Override
    public void run(ProgressIndicator indicator) {
        for (int i = 0; i < selectedLanguages.size(); i++) {
            GoogleSupportedLanguages language = selectedLanguages.get(i);
            GoogleTranslationJSON json = GoogleTranslationApi.getTranslationJSON(AndroidString.getAndroidStringValues(androidStrings),
                    language, GoogleSupportedLanguages.English);
            indicator.setFraction(indicatorFractionFrame * (double)(i));
            indicator.setText("Translating to " + language.name() + " (" + language.getLanguageDisplayName() + ")");

            // todo: write to file
        }
    }


}
