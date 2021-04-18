package com.solexgames.core.util.map;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.util.UUID;

@AllArgsConstructor
public class QrCodeMap extends MapRenderer {

    private final Image qrCodeImage;
    private final UUID uuid;

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if (player.getUniqueId().equals(this.uuid)) {
            canvas.drawImage(0, 0, this.qrCodeImage);
        }
    }
}
