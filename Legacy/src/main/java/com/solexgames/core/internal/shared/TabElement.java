package com.solexgames.core.internal.shared;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TabElement {

    private final List<TabEntry> entries = new ArrayList<>();

    /**
     * Get an entry by location in the tab element
     *
     * @param x the x axis
     * @param y the y axis
     * @return the entry
     */
    public TabEntry getEntry(int x, int y) {
        return this.entries.stream()
                .filter(entry -> entry.getX() == x && entry.getY() == y)
                .findFirst().orElseGet(() -> new TabEntry(x, y, "", -1));
    }

    /**
     * Add a new entry to the element
     *
     * @param index the index to get the axiss from
     * @param text  the text to display on the slot
     */
    public void add(int index, String text) {
        this.add(index, text, -1);
    }

    /**
     * Add a new entry to the element
     *
     * @param x    the x axis
     * @param y    the y axis
     * @param text the text to display on the slot
     */
    public void add(int x, int y, String text) {
        this.add(x, y, text, -1);
    }

    /**
     * Add a new entry to the element
     *
     * @param index the index to get the axiss from
     * @param text  the text to display on the slot
     * @param ping  the ping to display
     */
    public void add(int index, String text, int ping) {
        this.add(index % 4, index / 4, text, ping);
    }

    /**
     * Add a new entry to the element
     *
     * @param x    the x axis
     * @param y    the y axis
     * @param text the text to display on the slot
     * @param ping the ping to display
     */
    public void add(int x, int y, String text, int ping) {
        this.entries.add(new TabEntry(x, y, text, ping));
    }
}
