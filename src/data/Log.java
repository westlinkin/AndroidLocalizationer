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
 * Created by Wesley Lin on 12/3/14.
 */
public class Log {
    public static void i (String... params) {
        if (params == null)
            return;
        String out = "";
        for (int i = 0; i < params.length; i++) {
            out += params[i] + "\n";
        }
        System.out.println(out);
    }
}
