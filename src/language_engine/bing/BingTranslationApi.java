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

package language_engine.bing;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.Key;
import data.Log;
import language_engine.HttpUtils;
import module.SupportedLanguages;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley Lin on 12/3/14.
 */
public class BingTranslationApi {
    private static final String AUTH_URL = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13/";

    private static final String TRANSLATE_URL = "http://api.microsofttranslator.com/V2/Http.svc/TranslateArray";

    private static List<NameValuePair> getAccessTokenNameValuePair() {
        List<NameValuePair> params = new ArrayList<NameValuePair>(4);
        params.add(new BasicNameValuePair("client_id", Key.BING_CLIENT_ID));
        params.add(new BasicNameValuePair("client_secret", Key.BING_CLIENT_SECRET));
        params.add(new BasicNameValuePair("scope", Key.BING_CLIENT_SCOPE));
        params.add(new BasicNameValuePair("grant_type", Key.BING_CLIENT_GRANT_TYPE));
        return params;
    }

    public static String getAccessToken() {
        String postResult = HttpUtils.doHttpPost(AUTH_URL, getAccessTokenNameValuePair());
        JsonObject jsonObject = new JsonParser().parse(postResult).getAsJsonObject();
        return jsonObject.get("access_token").getAsString();
    }

    public static String getTranslatedStringArrays(String accessToken, List<String> querys, SupportedLanguages from, SupportedLanguages to) {
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

        String postResult = HttpUtils.doHttpPost(TRANSLATE_URL, xmlBody, headers);
        // todo fix the xml result, possibly create some classes, get the List<String> as result
        Log.i(postResult);
        return null;
    }
}
