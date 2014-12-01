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

package module;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Wesley Lin on 11/29/14.
 */
public enum GoogleSupportedLanguages {
    Afrikaans("af", "Afrikaans"),
    Albanian("sq", "Shqiptar"),
    Arabic("ar", "العربية"),
    Azerbaijani("az", "Azərbaycan"),
    Basque("eu", "Euskal"),
    Bengali("bn", "বাঙালি"),
    Belarusian("be", "Беларускі"),
    Bulgarian("bg", "Български"),
    Catalan("ca", "Català"),
    Chinese_Simplified("zh-CN", "简体中文"),
    Chinese_Traditional("zh-TW", "正體中文"),
    Croatian("hr", "Hrvatski"),
    Czech("cs", "Čeština"),
    Danish("da", "Dansk"),
    Dutch("nl", "Nederlands"),
    English("en", "English"),
    Esperanto("eo", "Esperanta"),
    Estonian("et", "Eesti"),
    Filipino("tl", "Pilipino"),
    Finnish("fi", "Suomi"),
    French("fr", "Français"),
    Galician("gl", "Galego"),
    Georgian("ka", "ქართული"),
    German("de", "Deutsch"),
    Greek("el", "Ελληνικά"),
    Gujarati("gu", "ગુજરાતી"),
    Haitian_Creole("ht", "Haitiancreole"),
    Hebrew("iw", "עברית"),
    Hindi("hi", "हिंदी"),
    Hungarian("hu", "Magyar"),
    Icelandic("is", "Icelandic"),
    Indonesian("id", "Indonesia"),
    Irish("ga", "Irish"),
    Italian("it", "Italiano"),
    Japanese("ja", "日本人"),
    Kannada("kn", "ಕನ್ನಡ"),
    Korean("ko", "한국의"),
    Latin("la", "Latina"),
    Latvian("lv", "Latvijas"),
    Lithuanian("lt", "Lietuvos"),
    Macedonian("mk", "Македонски"),
    Malay("ms", "Melayu"),
    Maltese("mt", "Malti"),
    Norwegian("no", "Norsk"),
    Persian("fa", "فارسی"),
    Polish("pl", "Polski"),
    Portuguese("pt", "Português"),
    Romanian("ro", "Român"),
    Russian("ru", "Русский"),
    Serbian("sr", "Српски"),
    Slovak("sk", "Slovenčina"),
    Slovenian("sl", "Slovenščina"),
    Spanish("es", "Español"),
    Swahili("sw", "Kiswahili"),
    Swedish("sv", "Svenska"),
    Tamil("ta", "தமிழ்"),
    Telugu("te", "తెలుగు"),
    Thai("th", "ไทย"),
    Turkish("tr", "Türk"),
    Ukrainian("uk", "Український"),
    Urdu("ur", "اردو"),
    Vietnamese("vi", "Tiếng Việt"),
    Welsh("cy", "Cymraeg"),
    Yiddish("yi", "ייִדיש")
    ;

    private String languageCode;
    private String languageDisplayName;

    GoogleSupportedLanguages(String languageCode, String languageDisplayName) {
        this.languageCode = languageCode;
        this.languageDisplayName = languageDisplayName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getLanguageDisplayName() {
        return languageDisplayName;
    }

    public static List<GoogleSupportedLanguages> getAllSupportedLanguages() {
        EnumSet<GoogleSupportedLanguages> all = EnumSet.allOf(GoogleSupportedLanguages.class);
        List<GoogleSupportedLanguages> data = new ArrayList<GoogleSupportedLanguages>(all.size());
        for (GoogleSupportedLanguages languages : all) {
            data.add(languages);
        }
        return data;
    }

    public String toString() {
        return name() + "(\"" + getLanguageCode() + "\", \"" + getLanguageDisplayName() + "\")";
    }
}
