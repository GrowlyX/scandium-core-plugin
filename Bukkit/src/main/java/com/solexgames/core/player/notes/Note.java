package com.solexgames.core.player.notes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@RequiredArgsConstructor
public class Note {

    public final String id;
    public final Date timestamp;

    public String value;

}
