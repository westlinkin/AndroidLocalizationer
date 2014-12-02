#Android Localizationer

This is a Android Studio/ IntelliJIdea plugin to localize your Android app, translate your string resources automactically.


##Features & Usage
Translate all your strings in your string resources(e.g. `strings.xml`) to your target languages automactically. Help developers localize their Android app easily, with just one click.

* Filter the strings you don't wanna translate by adding `NAL_` prefix to the `string key`, case sensitive. Like changing: 
`<string name="flurry_id">FLURRY_ID</string>`<br>
to<br>
`<string name="NAL_flurry_id">FLURRY_ID</string>`

More features are coming, please check [Todo](https://github.com/westlinkin/AndroidLocalizationer/blob/master/README.md#Todo).


##Downloads

##Change Notes

##Todo
* Multiple translation engine
* Plugin Settings
	* Choose the translation engine (translation API) you wanna use
	* Set translation engine (translation API)'s application key, in case of the API is runing out of quota
	* Filter the `string` key that you don't wanna translate, e.g. `app_name`, `some_api_key`
* Only show the `Convert to other languages` in the popup menu when right clicking the string resources, like [Google's Translation Editor](http://tools.android.com/recent/androidstudio087released) does


##License

	Copyright [2014] [Wesley Lin]

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

    	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.