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

package dev.mukulx.voidplots;

import dev.mukulx.voidplots.commands.PlotCommand;
import dev.mukulx.voidplots.commands.VoidPlotsCommand;
import dev.mukulx.voidplots.generator.PlotGenerator;
import dev.mukulx.voidplots.listeners.PlotProtectionListener;
import dev.mukulx.voidplots.listeners.PlotFlagListener;
import dev.mukulx.voidplots.listeners.PlotEntryListener;
import dev.mukulx.voidplots.listeners.PlayerJoinListener;
import dev.mukulx.voidplots.listeners.GUIListener;
import dev.mukulx.voidplots.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class VoidPlots extends JavaPlugin {
    
    private ConfigManager configManager;
    private MessageManager messageManager;
    private PlotManager plotManager;
    private DatabaseManager databaseManager;
    private GUIManager guiManager;
    private SchematicManager schematicManager;
    private NotificationManager notificationManager;
    
    @Override
    public void onEnable() {
        try {
            // Initialize managers
            this.configManager = new ConfigManager(this);
            this.messageManager = new MessageManager(this, configManager);
            this.databaseManager = new DatabaseManager(this);
            this.plotManager = new PlotManager(this, databaseManager);
            this.guiManager = new GUIManager(this, messageManager);
            this.schematicManager = new SchematicManager(this);
            this.notificationManager = new NotificationManager(this);
            
            // Load configurations
            configManager.loadConfig();
            messageManager.loadMessages();
            
            // Initialize database
            databaseManager.initialize();
            
            // Load plots from database
            plotManager.loadAllPlots();
            
            // Register commands
            registerCommands();
            
            // Register listeners
            registerListeners();
            
            // Register PlaceholderAPI expansion
            registerPlaceholders();
            
            getLogger().info("VoidPlots has been enabled successfully!");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable VoidPlots!", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        try {
            if (databaseManager != null) {
                databaseManager.close();
            }
            getLogger().info("VoidPlots has been disabled successfully!");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error during plugin disable!", e);
        }
    }
    
    @Override
    @Nullable
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        return new PlotGenerator(this);
    }
    
    private void registerCommands() {
        PlotCommand plotCommand = new PlotCommand(this);
        VoidPlotsCommand voidPlotsCommand = new VoidPlotsCommand(this);
        
        getCommand("plot").setExecutor(plotCommand);
        getCommand("plot").setTabCompleter(plotCommand);
        getCommand("voidplots").setExecutor(voidPlotsCommand);
        getCommand("voidplots").setTabCompleter(voidPlotsCommand);
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlotProtectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlotFlagListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlotEntryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }
    
    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                new dev.mukulx.voidplots.placeholders.VoidPlotsExpansion(this).register();
                getLogger().info("PlaceholderAPI expansion registered successfully!");
            } catch (Exception e) {
                getLogger().warning("Failed to register PlaceholderAPI expansion: " + e.getMessage());
            }
        }
    }
    
    public void reload() {
        configManager.loadConfig();
        messageManager.loadMessages();
    }
    
    // Getters
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    public PlotManager getPlotManager() {
        return plotManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public GUIManager getGuiManager() {
        return guiManager;
    }
    
    public SchematicManager getSchematicManager() {
        return schematicManager;
    }
    
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
}
