package module;

/**
 * Created by Wesley on 11/29/14.
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
    ChineseSimplified("zh-CN", "简体中文"),
    ChineseTraditional("zh-TW", "正體中文"),
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
    HaitianCreole("ht", "Haitiancreole"),
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
}
