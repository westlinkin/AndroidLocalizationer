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

package data;

import com.google.gson.Gson;
import com.intellij.openapi.util.io.StreamUtil;
import language_engine.google.GoogleSupportedLanguages;
import language_engine.google.GoogleTranslationJSON;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.util.IllegalFormatException;
import java.util.List;


/**
 * Created by Wesley Lin on 12/1/14.
 */
public class GoogleTranslationApi {
    public static final String BASE_TRANSLATION_URL = "https://www.googleapis.com/language/translate/v2?%s&target=%s&source=%s&key=%s";

    private static String doGet(String url) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse resp = httpClient.execute(httpGet);

            return StreamUtil.readText(resp.getEntity().getContent(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GoogleTranslationJSON getTranslationJSON(@NotNull List<String> querys,
                                                @NotNull GoogleSupportedLanguages targetLanguageCode,
                                                @NotNull GoogleSupportedLanguages sourceLanguageCode) {
        if (querys.isEmpty())
            return null;
        String query = "";
        for (int i = 0; i < querys.size(); i++) {
            query += ("q=" + URLEncoder.encode(querys.get(i)));
            if (i != querys.size() - 1) {
                query += "&";
            }
        }

        String url = null;
        try {
            url = String.format(BASE_TRANSLATION_URL, query,
                    targetLanguageCode.getLanguageCode(),
                    sourceLanguageCode.getLanguageCode(),
                    Key.GOOGLE_TRANSLATION_API_KEY);
        } catch (IllegalFormatException e) {
            e.printStackTrace();
        }
        if (url == null)
            return null;

        System.out.println("url: " + url);
        String result = doGet(url);
        System.out.println("do get result: " + result);
        return new Gson().fromJson(result, GoogleTranslationJSON.class);
    }

}
