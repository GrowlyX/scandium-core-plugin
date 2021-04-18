package com.solexgames.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LanguageType {

    ENGLISH("English", "en"),
    SPANISH("Spanish", "es"),
    FRENCH("French", "fr"),
    ITALIAN("Italian", "it"),
    GERMAN("German", "de");

    private final String languageName;
    private final String languageId;

    public static LanguageType getByName(String name) {
        return Arrays.stream(LanguageType.values())
                .filter(languageType -> name.equals(languageType.getLanguageName()))
                .findFirst().orElse(null);
    }

    public static LanguageType getById(String id) {
        return Arrays.stream(LanguageType.values()).filter(languageType -> id.equals(languageType.getLanguageId())).findFirst().orElse(null);
    }
}
