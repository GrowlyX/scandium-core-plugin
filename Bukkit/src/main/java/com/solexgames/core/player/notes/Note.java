package com.solexgames.core.player.notes;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Note {

    @SerializedName("_id")
    private final UUID id;

    private String title;
    private String value;

}
