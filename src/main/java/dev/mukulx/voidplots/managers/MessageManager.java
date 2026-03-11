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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

public class MessageManager {
    
    private final VoidPlots plugin;
    private final ConfigManager configManager;
    private FileConfiguration messages;
    
    public MessageManager(@NotNull VoidPlots plugin, @NotNull ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }
    
    public void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public void sendMessage(@NotNull CommandSender sender, @NotNull String path) {
        sendMessage(sender, path, null);
    }
    
    public void sendMessage(@NotNull CommandSender sender, @NotNull String path, @Nullable Map<String, String> placeholders) {
        String message = getMessage(path, placeholders);
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(message);
        }
    }
    
    @Nullable
    public String getMessage(@NotNull String path) {
        return getMessage(path, null);
    }
    
    @Nullable
    public String getMessage(@NotNull String path, @Nullable Map<String, String> placeholders) {
        String message = messages.getString(path);
        if (message == null) {
            plugin.getLogger().warning("Missing message: " + path);
            return null;
        }
        
        // Apply prefix
        boolean usePrefix = messages.getBoolean("settings.use-prefix", true);
        if (usePrefix) {
            String prefix = messages.getString("settings.prefix", "");
            message = message.replace("{prefix}", prefix);
        }
        
        // Apply placeholders
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        
        // Apply color codes
        message = ChatColor.translateAlternateColorCodes('&', message);
        
        return message;
    }
}
