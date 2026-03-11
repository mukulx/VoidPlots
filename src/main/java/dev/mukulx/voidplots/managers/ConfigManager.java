/**
 * VoidPlots - https://github.com/mukulx/VoidPlots
 *
 * Copyright (C) 2026 mukulx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package dev.mukulx.voidplots.managers;

import dev.mukulx.voidplots.VoidPlots;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    
    private final VoidPlots plugin;
    private FileConfiguration config;
    
    public ConfigManager(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    @NotNull
    public String getWorldName() {
        return config.getString("world.name", "plotworld");
    }
    
    public int getPlotSize() {
        return config.getInt("plot.size", 32);
    }
    
    public int getRoadWidth() {
        return config.getInt("plot.road-width", 7);
    }
    
    @NotNull
    public List<Material> getBorderBlocks() {
        List<String> blocks = config.getStringList("plot.border-blocks");
        List<Material> materials = new ArrayList<>();
        for (String block : blocks) {
            try {
                materials.add(Material.valueOf(block));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid border block: " + block);
            }
        }
        if (materials.isEmpty()) {
            materials.add(Material.STONE_BRICKS);
        }
        return materials;
    }
    
    @NotNull
    public List<Material> getRoadBlocks() {
        List<String> blocks = config.getStringList("plot.road-blocks");
        List<Material> materials = new ArrayList<>();
        for (String block : blocks) {
            try {
                materials.add(Material.valueOf(block));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid road block: " + block);
            }
        }
        if (materials.isEmpty()) {
            materials.add(Material.OAK_PLANKS);
        }
        return materials;
    }
    
    @NotNull
    public Material getFillBlock() {
        String block = config.getString("plot.fill-block", "GRASS_BLOCK");
        try {
            return Material.valueOf(block);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid fill block: " + block);
            return Material.GRASS_BLOCK;
        }
    }
    
    @NotNull
    public Material getBottomBlock() {
        String block = config.getString("plot.bottom-block", "BEDROCK");
        try {
            return Material.valueOf(block);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid bottom block: " + block);
            return Material.BEDROCK;
        }
    }
    
    public int getWallHeight() {
        return config.getInt("plot.wall-height", 64);
    }
    
    public int getMaxPlotsPerPlayer() {
        return config.getInt("limits.max-plots-per-player", 3);
    }
    
    public boolean isAutoClaimOnEnter() {
        return config.getBoolean("limits.auto-claim-on-enter", false);
    }
    
    public boolean isPlotChatEnabled() {
        return config.getBoolean("features.enable-plot-chat", true);
    }
    
    public boolean isPlotFlyEnabled() {
        return config.getBoolean("features.enable-plot-fly", true);
    }
    
    public boolean isWorldBorderEnabled() {
        return config.getBoolean("features.enable-world-border", true);
    }
    
    @NotNull
    public String getDatabaseType() {
        return config.getString("database.type", "sqlite");
    }
    
    @NotNull
    public String getDatabaseFile() {
        return config.getString("database.file", "plots.db");
    }
    
    // Notification settings
    public boolean isVisitNotificationEnabled() {
        return config.getBoolean("notifications.visit-notification", true);
    }
    
    public boolean isTrustedOnlineNotificationEnabled() {
        return config.getBoolean("notifications.trusted-online-notification", true);
    }
    
    public boolean isNotificationSoundEnabled() {
        return config.getBoolean("notifications.play-sound", true);
    }
    
    public String getNotificationSound() {
        return config.getString("notifications.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }
    
    // Auto-claim settings
    public boolean isAutoClaimOnFirstJoin() {
        return config.getBoolean("limits.auto-claim-on-first-join", false);
    }
}
