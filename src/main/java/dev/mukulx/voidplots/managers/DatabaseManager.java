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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mukulx.voidplots.VoidPlots;
import dev.mukulx.voidplots.models.Plot;
import dev.mukulx.voidplots.models.PlotComment;
import dev.mukulx.voidplots.models.PlotId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class DatabaseManager {
    
    private final VoidPlots plugin;
    private HikariDataSource dataSource;
    
    public DatabaseManager(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            
            String dbFile = plugin.getConfigManager().getDatabaseFile();
            String url = "jdbc:sqlite:" + new File(dataFolder, dbFile).getAbsolutePath();
            
            // Configure HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setDriverClassName("org.sqlite.JDBC");
            
            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            // SQLite specific settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            
            // Connection pool name
            config.setPoolName("VoidPlots-Pool");
            
            // Initialize the data source
            dataSource = new HikariDataSource(config);
            
            // Create tables
            createTables();
            
            plugin.getLogger().info("Database initialized with HikariCP connection pool!");
            plugin.getLogger().info("Pool size: " + config.getMaximumPoolSize() + " connections");
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize database!", e);
        }
    }
    
    private void createTables() {
        String plotsTable = "CREATE TABLE IF NOT EXISTS plots (" +
                "id TEXT PRIMARY KEY," +
                "owner TEXT," +
                "created_at BIGINT" +
                ")";
        
        String trustedTable = "CREATE TABLE IF NOT EXISTS plot_trusted (" +
                "plot_id TEXT," +
                "player_uuid TEXT," +
                "PRIMARY KEY (plot_id, player_uuid)," +
                "FOREIGN KEY (plot_id) REFERENCES plots(id) ON DELETE CASCADE" +
                ")";
        
        String deniedTable = "CREATE TABLE IF NOT EXISTS plot_denied (" +
                "plot_id TEXT," +
                "player_uuid TEXT," +
                "PRIMARY KEY (plot_id, player_uuid)," +
                "FOREIGN KEY (plot_id) REFERENCES plots(id) ON DELETE CASCADE" +
                ")";
        
        String flagsTable = "CREATE TABLE IF NOT EXISTS plot_flags (" +
                "plot_id TEXT," +
                "flag_name TEXT," +
                "flag_value INTEGER," +
                "PRIMARY KEY (plot_id, flag_name)," +
                "FOREIGN KEY (plot_id) REFERENCES plots(id) ON DELETE CASCADE" +
                ")";
        
        String ratingsTable = "CREATE TABLE IF NOT EXISTS plot_ratings (" +
                "plot_id TEXT," +
                "player_uuid TEXT," +
                "rating INTEGER CHECK(rating >= 1 AND rating <= 5)," +
                "PRIMARY KEY (plot_id, player_uuid)," +
                "FOREIGN KEY (plot_id) REFERENCES plots(id) ON DELETE CASCADE" +
                ")";
        
        String commentsTable = "CREATE TABLE IF NOT EXISTS plot_comments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "plot_id TEXT," +
                "author_uuid TEXT," +
                "author_name TEXT," +
                "message TEXT," +
                "timestamp BIGINT," +
                "FOREIGN KEY (plot_id) REFERENCES plots(id) ON DELETE CASCADE" +
                ")";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(plotsTable);
            stmt.execute(trustedTable);
            stmt.execute(deniedTable);
            stmt.execute(flagsTable);
            stmt.execute(ratingsTable);
            stmt.execute(commentsTable);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create tables!", e);
        }
    }
    
    public void savePlot(@NotNull Plot plot) {
        String sql = "INSERT OR REPLACE INTO plots (id, owner, created_at) VALUES (?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plot.getId().toString());
            stmt.setString(2, plot.getOwner() != null ? plot.getOwner().toString() : null);
            stmt.setLong(3, plot.getCreatedAt());
            stmt.executeUpdate();
            
            // Save related data
            saveTrustedPlayers(conn, plot);
            saveDeniedPlayers(conn, plot);
            saveFlags(conn, plot);
            saveRatings(conn, plot);
            saveComments(conn, plot);
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save plot: " + plot.getId(), e);
        }
    }
    
    private void saveTrustedPlayers(@NotNull Connection conn, @NotNull Plot plot) throws SQLException {
        // Delete existing trusted players
        String deleteSql = "DELETE FROM plot_trusted WHERE plot_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, plot.getId().toString());
            stmt.executeUpdate();
        }
        
        // Insert trusted players
        if (!plot.getTrusted().isEmpty()) {
            String insertSql = "INSERT INTO plot_trusted (plot_id, player_uuid) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (UUID uuid : plot.getTrusted()) {
                    stmt.setString(1, plot.getId().toString());
                    stmt.setString(2, uuid.toString());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
    
    @Nullable
    public Plot loadPlot(@NotNull PlotId plotId) {
        String sql = "SELECT * FROM plots WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plotId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ownerStr = rs.getString("owner");
                    UUID owner = ownerStr != null ? UUID.fromString(ownerStr) : null;
                    long createdAt = rs.getLong("created_at");
                    
                    Set<UUID> trusted = loadTrustedPlayers(conn, plotId);
                    Set<UUID> denied = loadDeniedPlayers(conn, plotId);
                    Map<String, Boolean> flags = loadFlags(conn, plotId);
                    Map<UUID, Integer> ratings = loadRatings(conn, plotId);
                    List<PlotComment> comments = loadComments(conn, plotId);
                    
                    return new Plot(plotId, owner, trusted, denied, createdAt, flags, 
                                  comments, ratings, null, null);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load plot: " + plotId, e);
        }
        
        return null;
    }
    
    @NotNull
    private Set<UUID> loadTrustedPlayers(@NotNull Connection conn, @NotNull PlotId plotId) throws SQLException {
        Set<UUID> trusted = new HashSet<>();
        String sql = "SELECT player_uuid FROM plot_trusted WHERE plot_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plotId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trusted.add(UUID.fromString(rs.getString("player_uuid")));
                }
            }
        }
        
        return trusted;
    }
    
    private void saveDeniedPlayers(@NotNull Connection conn, @NotNull Plot plot) throws SQLException {
        String deleteSql = "DELETE FROM plot_denied WHERE plot_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, plot.getId().toString());
            stmt.executeUpdate();
        }
        
        if (!plot.getDenied().isEmpty()) {
            String insertSql = "INSERT INTO plot_denied (plot_id, player_uuid) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (UUID uuid : plot.getDenied()) {
                    stmt.setString(1, plot.getId().toString());
                    stmt.setString(2, uuid.toString());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
    
    @NotNull
    private Set<UUID> loadDeniedPlayers(@NotNull Connection conn, @NotNull PlotId plotId) throws SQLException {
        Set<UUID> denied = new HashSet<>();
        String sql = "SELECT player_uuid FROM plot_denied WHERE plot_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plotId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    denied.add(UUID.fromString(rs.getString("player_uuid")));
                }
            }
        }
        
        return denied;
    }
    
    private void saveFlags(@NotNull Connection conn, @NotNull Plot plot) throws SQLException {
        String deleteSql = "DELETE FROM plot_flags WHERE plot_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, plot.getId().toString());
            stmt.executeUpdate();
        }
        
        if (!plot.getFlags().isEmpty()) {
            String insertSql = "INSERT INTO plot_flags (plot_id, flag_name, flag_value) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (Map.Entry<String, Boolean> entry : plot.getFlags().entrySet()) {
                    stmt.setString(1, plot.getId().toString());
                    stmt.setString(2, entry.getKey());
                    stmt.setInt(3, entry.getValue() ? 1 : 0);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
    
    @NotNull
    private Map<String, Boolean> loadFlags(@NotNull Connection conn, @NotNull PlotId plotId) throws SQLException {
        Map<String, Boolean> flags = new HashMap<>();
        String sql = "SELECT flag_name, flag_value FROM plot_flags WHERE plot_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plotId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    flags.put(rs.getString("flag_name"), rs.getInt("flag_value") == 1);
                }
            }
        }
        
        return flags;
    }
    
    private void saveRatings(@NotNull Connection conn, @NotNull Plot plot) throws SQLException {
        String deleteSql = "DELETE FROM plot_ratings WHERE plot_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, plot.getId().toString());
            stmt.executeUpdate();
        }
        
        if (!plot.getRatings().isEmpty()) {
            String insertSql = "INSERT INTO plot_ratings (plot_id, player_uuid, rating) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (Map.Entry<UUID, Integer> entry : plot.getRatings().entrySet()) {
                    stmt.setString(1, plot.getId().toString());
                    stmt.setString(2, entry.getKey().toString());
                    stmt.setInt(3, entry.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
    
    @NotNull
    private Map<UUID, Integer> loadRatings(@NotNull Connection conn, @NotNull PlotId plotId) throws SQLException {
        Map<UUID, Integer> ratings = new HashMap<>();
        String sql = "SELECT player_uuid, rating FROM plot_ratings WHERE plot_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plotId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.put(UUID.fromString(rs.getString("player_uuid")), rs.getInt("rating"));
                }
            }
        }
        
        return ratings;
    }
    
    public void deletePlot(@NotNull PlotId plotId) {
        String sql = "DELETE FROM plots WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plotId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to delete plot: " + plotId, e);
        }
    }
    
    @NotNull
    public List<Plot> getPlayerPlots(@NotNull UUID playerUuid) {
        List<Plot> plots = new ArrayList<>();
        String sql = "SELECT id FROM plots WHERE owner = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PlotId plotId = PlotId.fromString(rs.getString("id"));
                    Plot plot = loadPlot(plotId);
                    if (plot != null) {
                        plots.add(plot);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load player plots", e);
        }
        
        return plots;
    }
    
    @NotNull
    public List<Plot> getAllPlots() {
        List<Plot> plots = new ArrayList<>();
        String sql = "SELECT id FROM plots";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                PlotId plotId = PlotId.fromString(rs.getString("id"));
                Plot plot = loadPlot(plotId);
                if (plot != null) {
                    plots.add(plot);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load all plots", e);
        }
        
        return plots;
    }
    
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection pool closed successfully!");
        }
    }
    
    @NotNull
    public HikariDataSource getDataSource() {
        return dataSource;
    }
    
    private void saveComments(@NotNull Connection conn, @NotNull Plot plot) throws SQLException {
        String deleteSql = "DELETE FROM plot_comments WHERE plot_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, plot.getId().toString());
            stmt.executeUpdate();
        }
        
        if (!plot.getComments().isEmpty()) {
            String insertSql = "INSERT INTO plot_comments (plot_id, author_uuid, author_name, message, timestamp) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (PlotComment comment : plot.getComments()) {
                    stmt.setString(1, plot.getId().toString());
                    stmt.setString(2, comment.getAuthorUuid().toString());
                    stmt.setString(3, comment.getAuthorName());
                    stmt.setString(4, comment.getMessage());
                    stmt.setLong(5, comment.getTimestamp());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
    
    @NotNull
    private List<PlotComment> loadComments(@NotNull Connection conn, @NotNull PlotId plotId) throws SQLException {
        List<PlotComment> comments = new ArrayList<>();
        String sql = "SELECT author_uuid, author_name, message, timestamp FROM plot_comments WHERE plot_id = ? ORDER BY timestamp DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plotId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(new PlotComment(
                        UUID.fromString(rs.getString("author_uuid")),
                        rs.getString("author_name"),
                        rs.getString("message"),
                        rs.getLong("timestamp")
                    ));
                }
            }
        }
        
        return comments;
    }
}
