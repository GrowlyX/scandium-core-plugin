package vip.potclub.core.enums;

import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.Arrays;

@Getter
public enum LanguageType {

    ENGLISH("English", "en"),
    SPANISH("Spanish", "es"),
    FRENCH("French", "fr"),
    ITALIAN("Italian", "it"),
    GERMAN("German", "de");

    private final String languageName;
    private final String languageId;

    @ConstructorProperties("languageName")
    LanguageType(String languageName, String languageId) {
        this.languageName = languageName;
        this.languageId = languageId;
    }

    public static LanguageType getByName(String name) {
        return Arrays.stream(LanguageType.values()).filter(languageType -> name.equals(languageType.getLanguageName())).findFirst().orElse(null);
    }

    public static LanguageType getById(String id) {
        return Arrays.stream(LanguageType.values()).filter(languageType -> id.equals(languageType.getLanguageId())).findFirst().orElse(null);
    }
}
