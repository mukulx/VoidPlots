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
import dev.mukulx.voidplots.models.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NotificationManager {
    
    private final VoidPlots plugin;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    
    public NotificationManager(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.configManager = plugin.getConfigManager();
    }
    
    public void notifyPlotVisit(@NotNull Plot plot, @NotNull Player visitor) {
        if (!configManager.isVisitNotificationEnabled()) {
            return;
        }
        
        UUID owner = plot.getOwner();
        if (owner == null || owner.equals(visitor.getUniqueId())) {
            return;
        }
        
        Player ownerPlayer = Bukkit.getPlayer(owner);
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            String message = messageManager.getMessage("notification.visit")
                    .replace("{player}", visitor.getName())
                    .replace("{plot}", plot.getId().toString());
            ownerPlayer.sendMessage(message);
            
            if (configManager.isNotificationSoundEnabled()) {
                playNotificationSound(ownerPlayer);
            }
        }
    }
    
    public void notifyTrustedPlayerOnline(@NotNull Plot plot, @NotNull Player trustedPlayer) {
        if (!configManager.isTrustedOnlineNotificationEnabled()) {
            return;
        }
        
        UUID owner = plot.getOwner();
        if (owner == null) {
            return;
        }
        
        Player ownerPlayer = Bukkit.getPlayer(owner);
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            String message = messageManager.getMessage("notification.trusted-online")
                    .replace("{player}", trustedPlayer.getName())
                    .replace("{plot}", plot.getId().toString());
            ownerPlayer.sendMessage(message);
            
            if (configManager.isNotificationSoundEnabled()) {
                playNotificationSound(ownerPlayer);
            }
        }
    }
    
    public void notifyPlotRated(@NotNull Plot plot, @NotNull Player rater, int rating) {
        UUID owner = plot.getOwner();
        if (owner == null || owner.equals(rater.getUniqueId())) {
            return;
        }
        
        Player ownerPlayer = Bukkit.getPlayer(owner);
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            String message = messageManager.getMessage("notification.rated")
                    .replace("{player}", rater.getName())
                    .replace("{rating}", String.valueOf(rating))
                    .replace("{plot}", plot.getId().toString());
            ownerPlayer.sendMessage(message);
            
            if (configManager.isNotificationSoundEnabled()) {
                playNotificationSound(ownerPlayer);
            }
        }
    }
    
    private void playNotificationSound(@NotNull Player player) {
        try {
            String soundName = configManager.getNotificationSound();
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid notification sound: " + configManager.getNotificationSound());
        }
    }
}
