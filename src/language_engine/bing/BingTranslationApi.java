/*
 * Copyright 2014-2015 Wesley Lin
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

package language_engine.bing;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intellij.ide.util.PropertiesComponent;
import data.Key;
import data.Log;
import data.StorageDataKey;
import language_engine.HttpUtils;
import module.SupportedLanguages;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley Lin on 12/3/14.
 */
public class BingTranslationApi {
    private static final String ENCODING = "UTF-8";

    private static final String AUTH_URL = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13/";

    private static final String TRANSLATE_URL = "http://api.microsofttranslator.com/V2/Ajax.svc/TranslateArray?";

    private static List<NameValuePair> getAccessTokenNameValuePair() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>(4);
        params.add(new BasicNameValuePair("client_id",
                propertiesComponent.getValue(StorageDataKey.BingClientIdStored, Key.BING_CLIENT_ID)));
        params.add(new BasicNameValuePair("client_secret",
                propertiesComponent.getValue(StorageDataKey.BingClientSecretStored, Key.BING_CLIENT_SECRET)));
        params.add(new BasicNameValuePair("scope", Key.BING_CLIENT_SCOPE));
        params.add(new BasicNameValuePair("grant_type", Key.BING_CLIENT_GRANT_TYPE));
        return params;
    }

    public static String getAccessToken() {
        String postResult = HttpUtils.doHttpPost(AUTH_URL, getAccessTokenNameValuePair());
        JsonObject jsonObject = new JsonParser().parse(postResult).getAsJsonObject();
        if (jsonObject.get("error") == null) {
            return jsonObject.get("access_token").getAsString();
        }
        return null;
    }

    protected static final String PARAM_APP_ID = "appId=",
            PARAM_TO_LANG = "&to=",
            PARAM_FROM_LANG = "&from=",
            PARAM_TEXT_ARRAY = "&texts=";

    /**
     * using AJAX api now: http://msdn.microsoft.com/en-us/library/ff512402.aspx
     * @param accessToken
     * @param querys
     * @param from
     * @param to
     * @return list of String, which is the result
     */
    public static List<String> getTranslatedStringArrays2(String accessToken, List<String> querys, SupportedLanguages from, SupportedLanguages to) {
//        Log.i(accessToken);
        String url = generateUrl(accessToken, querys, from, to);
        Header[] headers = new Header[]{
                new BasicHeader("Authorization", "Bearer " + accessToken),
                new BasicHeader("Content-Type", "text/plain; charset=" + ENCODING),
                new BasicHeader("Accept-Charset", ENCODING)
        };

        String getResult = HttpUtils.doHttpGet(url, headers);
//        Log.i(getResult);
        JsonArray jsonArray = null;
        try {
            jsonArray = new JsonParser().parse(getResult).getAsJsonArray();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (jsonArray == null)
            return null;

        List<String> result = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            String translatedText = jsonArray.get(i).getAsJsonObject().get("TranslatedText").getAsString();
            if (translatedText == null) {
                result.add("");
            } else {
                result.add(StringEscapeUtils.unescapeJava(translatedText));
            }
        }
        return result;
    }

    private static String generateUrl(String accessToken, List<String> querys, SupportedLanguages from, SupportedLanguages to) {
        String[] texts = new String[]{};
        texts = querys.toArray(texts);
        for (int i = 0; i < texts.length; i++) {
            texts[i] = StringEscapeUtils.escapeJava(texts[i]);
        }

        try {
            final String params =
                    (accessToken != null ? PARAM_APP_ID + URLEncoder.encode("Bearer " + accessToken, ENCODING) : "")
                            + PARAM_FROM_LANG + URLEncoder.encode(from.getLanguageCode(), ENCODING)
                            + PARAM_TO_LANG + URLEncoder.encode(to.getLanguageCode(), ENCODING)
                            + PARAM_TEXT_ARRAY + URLEncoder.encode(buildStringArrayParam(texts), ENCODING);

            return TRANSLATE_URL + params;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String buildStringArrayParam(Object[] values) {
        StringBuilder targetString = new StringBuilder("[\"");
        String value;
        for (Object obj : values) {
            if (obj != null) {
                value = obj.toString();
                if (value.length() != 0) {
                    if (targetString.length() > 2)
                        targetString.append(",\"");
                    targetString.append(value);
                    targetString.append("\"");
                }
            }
        }
        targetString.append("]");
        return targetString.toString();
    }


    /**
     * @deprecated
     * using @getTranslatedStringArrays2 now
     */
    public static List<String> getTranslatedStringArrays(String accessToken, List<String> querys, SupportedLanguages from, SupportedLanguages to) {
        String xmlBodyTop = "<TranslateArrayRequest>\n" +
                "  <AppId />\n" +
                "  <From>%s</From>\n" +
                "  <Options>\n" +
                "    <Category xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />\n" +
                "    <ContentType xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\">text/plain</ContentType>\n" +
                "    <ReservedFlags xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />\n" +
                "    <State xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />\n" +
                "    <Uri xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />\n" +
                "    <User xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />\n" +
                "  </Options>\n" +
                "  <Texts>\n";

        String xmlBodyMid = "<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">%s</string>\n";
        String xmlBodyBot = "    </Texts>\n" +
                "  <To>%s</To>\n" +
                "</TranslateArrayRequest>";

        String xmlBodyStrings = "";
        for (String query : querys) {
            xmlBodyStrings += String.format(xmlBodyMid, query);
        }

        String xmlBody = String.format(xmlBodyTop, from.getLanguageCode())
                + xmlBodyStrings
                + String.format(xmlBodyBot, to.getLanguageCode());

        Header[] headers = new Header[]{
            new BasicHeader("Authorization", "Bearer " + accessToken),
            new BasicHeader("Content-Type", "text/xml")
        };

        Log.i("Bearer " + accessToken);
        Log.i("xml body: " + xmlBody);

        String postResult = HttpUtils.doHttpPost(TRANSLATE_URL, xmlBody, headers);
        Log.i("post result: " + postResult);

        List<TranslateArrayResponse> translateArrayResponses = BingResultParser.parseTranslateArrayResponse(postResult);

        List<String> result = new ArrayList<String>();
        for (TranslateArrayResponse translateArrayResponse : translateArrayResponses) {
            result.add(translateArrayResponse.getTranslatedText());
        }
        return result;
    }
}
