#Android Localizationer

This is a Android Studio/ IntelliJ IDEA plugin to localize your Android app, translate your string resources automactically.

Translate all your strings in your string resources(e.g. `strings.xml`) to your target languages automactically. Help developers localize their Android app easily, with just one click.


##Usage

Right click the strings resource file, choose 'Convert to other languages'.<br>
![img](https://raw.githubusercontent.com/westlinkin/AndroidLocalizationer/master/screen_shot_3.png)<br>
Then check the target languages.<br>
![img](https://raw.githubusercontent.com/westlinkin/AndroidLocalizationer/master/screen_shot_2.png) 
<br>
After clicking `OK`, the string resources will be translated and created in the correct value folder.

##Feature

* Filter the strings you don't wanna translate by adding `NAL_` prefix to the `string key`, case sensitive. Change:<br>
`<string name="flurry_id">FLURRY_ID</string>`<br>
to<br>
`<string name="NAL_flurry_id">FLURRY_ID</string>`

* Filter the strings you don't wanna translate by adding `filter rule` in plugin settings interface

* Set client id or client secret for Microsoft Translator, in case of running out of quota. 
	* [How to get Microsoft Translator client id and client secret?](http://blogs.msdn.com/b/translation/p/gettingstarted1.aspx)
	
	![img](https://raw.githubusercontent.com/westlinkin/AndroidLocalizationer/master/screen_shot_5.png) 
	 
* Set Google API key to use Google Translation API, you need to do [this](https://cloud.google.com/translate/v2/getting_started#intro), at the **Public API access** section of the **Credentials** page, create a new **`Browser key`**. Please **NOTE** that this is a [paid service](https://cloud.google.com/translate/v2/pricing).

More features are coming, please check [Todo](https://github.com/westlinkin/AndroidLocalizationer#todo).

##Warning
* Currently, Android Localizationer only support translate **English** to other languages
* The result may **not** meet your standards due to the Translation API that this plugin is using, so keep your string resources **as simple as possible**


##Downloads
You can download the plugin [here](https://github.com/westlinkin/AndroidLocalizationer/raw/master/AndroidLocalizationer.zip).

To Install the plugin, please check [IntelliJ IDEA Web Help](https://www.jetbrains.com/idea/help/installing-updating-and-uninstalling-repository-plugins.html#d1282549e185).

##ChangeLog
Version 0.1.3

* Fix bug: translation fails when there are too many string resources
* Fix bug: translation fails when there are special symbols, like `€`
* Fix bug: translation fails when there are special tags, like `<u>`
* Fix Java escape problems in MS Translator

Version 0.1.2

* Add Google Translation API support. Please **NOTE** that this is a [paid service](https://cloud.google.com/translate/v2/pricing).
* Fix bug: show error when opening the translated strings.xml file

Version 0.1.1

* Fix bug: when translate to more than one language, only the first target language will be translated correctly
* Fix bug: filter rule in plugin settings cannot be filtered
* Fix bug: wrongly show 'Quota exceed' error dialog when both not running out of quota and no strings need to be translated
     

Version 0.1.0

* Add **filter rule** setting in plugin settings interface, filter strings you don't wanna translate
* Fix a possible throwable when automatically open the translated strings.xml file

Version 0.0.3

* Only show 'Convert to other languages' menu on `strings.xml` files, current only `strings.xml` file under `values` or `values-en` folders.* 
* Add an icon before 'Convert to other languages' menu* 
* Add a plugin settings interface, client id and client secret for Microsoft Translator can be set by users* 
* Popup error message when Microsoft Translator quota exceed or client id/ client secret is invalid

Version 0.0.2

* Fix string error on the popup dialog

Version 0.0.1

* Publish project


##Todo
* Multiple translation engine
* Plugin Settings
	* <del>Choose the translation engine (translation API) you wanna use
	* <del>Set translation engine (translation API)'s application key, in case of the API is runing out of quota
	* <del>Filter the `string` key that you don't wanna translate, e.g. `app_name`, `some_api_key`
* <del>Only show the `Convert to other languages` in the popup menu when right clicking the string resources, like [Google's Translation Editor](http://tools.android.com/recent/androidstudio087released) does
* Support more source languages
* Support string arrays


##License

	Copyright 2014-2015 Wesley Lin

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

    	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.