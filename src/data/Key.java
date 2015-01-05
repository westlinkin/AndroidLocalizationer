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

package data;

/**
 * Created by Wesley Lin on 11/29/14.
 */
public class Key {

    // bing, clientId and clientSecret can be set by users
    public static final String BING_CLIENT_ID = "android_localizationer";
    public static final String BING_CLIENT_SECRET = "eQiD1XOQCKToGLWMl0GXuWZb2cQJqYIwid8UPhln5CY=";
    public static final String BING_CLIENT_SCOPE = "http://api.microsofttranslator.com";
    public static final String BING_CLIENT_GRANT_TYPE = "client_credentials";


    // other than api keys
    public static final String NO_NEED_TRANSLATION_ANDROID_STRING_PREFIX = "NAL_";
}
