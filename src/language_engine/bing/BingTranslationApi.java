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

import data.Key;
import data.Log;
import language_engine.HttpUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley Lin on 12/3/14.
 */
public class BingTranslationApi {
    private static final String AUTH_URL = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13/";

    private static List<NameValuePair> getAccessTokenNameValuePair() {
        List<NameValuePair> params = new ArrayList<NameValuePair>(4);
        params.add(new BasicNameValuePair("client_id", Key.BING_CLIENT_ID));
        params.add(new BasicNameValuePair("client_secret", Key.BING_CLIENT_SECRET));
        params.add(new BasicNameValuePair("scope", Key.BING_CLIENT_SCOPE));
        params.add(new BasicNameValuePair("grant_type", Key.BING_CLIENT_GRANT_TYPE));
        return params;
    }

    private static String getAccessToken() {
        String postResult = HttpUtils.doHttpPost(AUTH_URL, getAccessTokenNameValuePair());
        Log.i(postResult);
        return null;
    }
}
