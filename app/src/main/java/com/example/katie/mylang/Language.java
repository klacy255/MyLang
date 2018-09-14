package com.example.katie.mylang;
/**
 * Language.java
 * Purpose: Enum class for handling google translation language codes
 *
 * @author katie
 * @version 1.0 4/10/17.
 */
public enum Language {

    AUTO_DETECT("en"), AFRIKAANS("af"), ALBANIAN("sq"), AMHARIC("am"), ARABIC("ar"), ARMENIAN("hy"),
    AZERBAIJANI("az"), BASQUE("eu"), BELARUSIAN("be"), BENGALI("bn"), BIHARI("bh"), BULGARIAN("bg"),
    BURMESE("my"), CATALAN("ca"), CHEROKEE("chr"), CHINESE("zh"), CHINESE_SIMPLIFIED("zh-CN"),
    CHINESE_TRADITIONAL("zh-TW"), CROATIAN("hr"), CZECH("cs"), DANISH("da"), DHIVEHI("dv"),
    DUTCH("nl"), ENGLISH("en"), ESPERANTO("eo"), ESTONIAN("et"), FILIPINO("tl"), FINNISH("fi"),
    FRENCH("fr"), GALICIAN("gl"), GEORGIAN("ka"), GERMAN("de"), GREEK("el"), GUARANI("gn"),
    GUJARATI("gu"), HEBREW("iw"), HINDI("hi"), HUNGARIAN("hu"), ICELANDIC("is"), INDONESIAN("id"),
    INUKTITUT("iu"), IRISH("ga"), ITALIAN("it"), JAPANESE("ja"), KANNADA("kn"), KAZAKH("kk"),
    KHMER("km"), KOREAN("ko"), KURDISH("ku"), KYRGYZ("ky"), LAOTHIAN("lo"), LATVIAN("lv"),
    LITHUANIAN("lt"), MACEDONIAN("mk"), MALAY("ms"), MALAYALAM("ml"), MALTESE("mt"), MARATHI("mr"),
    MONGOLIAN("mn"), NEPALI("ne"), NORWEGIAN("no"), ORIYA("or"), PASHTO("ps"), PERSIAN("fa"),
    POLISH("pl"), PORTUGUESE("pt"), PUNJABI("pa"), ROMANIAN("ro"), RUSSIAN("ru"), SANSKRIT("sa"),
    SERBIAN("sr"), SINDHI("sd"), SINHALESE("si"), SLOVAK("sk"), SLOVENIAN("sl"), SPANISH("es"),
    SWAHILI("sw"), SWEDISH("sv"), TAJIK("tg"), TAMIL("ta"), TAGALOG("tl"), TELUGU("te"), THAI("th"),
    TIBETAN("bo"), TURKISH("tr"), UKRANIAN("uk"), URDU("ur"), UZBEK("uz"), UIGHUR("ug"),
    VIETNAMESE("vi"), WELSH("cy"), YIDDISH("yi");

    public static final String[] LANGUAGE_NAMES = new String[]{"ENGLISH", "AFRIKAANS", "ALBANIAN",
            "AMHARIC", "ARABIC", "ARMENIAN", "AZERBAIJANI", "BASQUE", "BELARUSIAN", "BENGALI",
            "BIHARI", "BULGARIAN", "BURMESE", "CATALAN", "CHEROKEE", "CHINESE", "CHINESE_SIMPLIFIED",
            "CHINESE_TRADITIONAL", "CROATIAN", "CZECH", "DANISH", "DHIVEHI", "DUTCH", "ESPERANTO",
            "ESTONIAN", "FILIPINO", "FINNISH", "FRENCH", "GALICIAN", "GEORGIAN", "GERMAN", "GREEK",
            "GUARANI", "GUJARATI", "HEBREW", "HINDI", "HUNGARIAN", "ICELANDIC", "INDONESIAN",
            "INUKTITUT", "IRISH", "ITALIAN", "JAPANESE", "KANNADA", "KAZAKH", "KHMER", "KOREAN",
            "KURDISH", "KYRGYZ", "LAOTHIAN", "LATVIAN", "LITHUANIAN", "MACEDONIAN", "MALAY",
            "MALAYALAM", "MALTESE", "MARATHI", "MONGOLIAN", "NEPALI", "NORWEGIAN", "ORIYA", "PASHTO",
            "PERSIAN", "POLISH", "PORTUGUESE", "PUNJABI", "ROMANIAN", "RUSSIAN", "SANSKRIT", "SERBIAN",
            "SINDHI", "SINHALESE", "SLOVAK", "SLOVENIAN", "SPANISH", "SWAHILI", "SWEDISH", "TAJIK",
            "TAMIL", "TAGALOG", "TELUGU", "THAI", "TIBETAN", "TURKISH", "UKRANIAN", "URDUK", "UZBEK",
            "IGHUR", "VIETNAMESE", "WELSH", "YIDDISH"};

    private final String language;

    Language(final String language) {
        this.language = language;
    }

    public static Language fromString(final String langName) {
        for (Language l : values()) {
            if (l.toString().equals(langName)) {
                return l;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return language;
    }
}
