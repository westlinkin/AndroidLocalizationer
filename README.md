#Android Localizationer

This is a Android Studio/ IntelliJIdea plugin to localize your Android app, translate your string resources automactically.

Translate all your strings in your string resources(e.g. `strings.xml`) to your target languages automactically. Help developers localize their Android app easily, with just one click.


##Usage

Right click the strings resource file, choose 'Convert to other languages'.<br>
![img](https://raw.githubusercontent.com/westlinkin/AndroidLocalizationer/master/screen_shot_1.png)<br>
Then check the target languages.<br>
![img](https://raw.githubusercontent.com/westlinkin/AndroidLocalizationer/master/screen_shot_2.png) 
<br>
After clicking `OK`, the string resources will be translated and created in the correct value folder.

##Feature

* Filter the strings you don't wanna translate by adding `NAL_` prefix to the `string key`, case sensitive. Change:<br>
`<string name="flurry_id">FLURRY_ID</string>`<br>
to<br>
`<string name="NAL_flurry_id">FLURRY_ID</string>`

More features are coming, please check [Todo](https://github.com/westlinkin/AndroidLocalizationer#todo).

##Warning
* Currently, Android Localizationer only support translate **English** to other languages
* The result may **not** meet your standards due to the Translation API that this plugin is using, so keep your string resources **as simple as possible**


##Downloads
You can download the plugin [here](https://github.com/westlinkin/AndroidLocalizationer/raw/master/AndroidLocalizationer.jar).

To Install the plugin, please check [IntelliJ IDEA Web Help](https://www.jetbrains.com/idea/help/installing-updating-and-uninstalling-repository-plugins.html#d1282549e185).

##ChangeLog

##Todo
* Multiple translation engine
* Plugin Settings
	* Choose the translation engine (translation API) you wanna use
	* Set translation engine (translation API)'s application key, in case of the API is runing out of quota
	* Filter the `string` key that you don't wanna translate, e.g. `app_name`, `some_api_key`
* Only show the `Convert to other languages` in the popup menu when right clicking the string resources, like [Google's Translation Editor](http://tools.android.com/recent/androidstudio087released) does
* Support more source languages


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