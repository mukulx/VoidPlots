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

package dev.mukulx.voidplots.models;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Plot {
    
    private final PlotId id;
    private UUID owner;
    private final Set<UUID> trusted;
    private final Set<UUID> denied;
    private final long createdAt;
    private final Map<String, Boolean> flags;
    private final List<PlotComment> comments;
    private final Map<UUID, Integer> ratings;
    private String alias;
    private String description;
    
    public Plot(@NotNull PlotId id, @Nullable UUID owner) {
        this.id = id;
        this.owner = owner;
        this.trusted = new HashSet<>();
        this.denied = new HashSet<>();
        this.createdAt = System.currentTimeMillis();
        this.flags = new HashMap<>();
        this.comments = new ArrayList<>();
        this.ratings = new HashMap<>();
        initializeDefaultFlags();
    }
    
    public Plot(@NotNull PlotId id, @Nullable UUID owner, @NotNull Set<UUID> trusted, long createdAt) {
        this.id = id;
        this.owner = owner;
        this.trusted = new HashSet<>(trusted);
        this.denied = new HashSet<>();
        this.createdAt = createdAt;
        this.flags = new HashMap<>();
        this.comments = new ArrayList<>();
        this.ratings = new HashMap<>();
        initializeDefaultFlags();
    }
    
    public Plot(@NotNull PlotId id, @Nullable UUID owner, @NotNull Set<UUID> trusted, @NotNull Set<UUID> denied,
                long createdAt, @NotNull Map<String, Boolean> flags, @NotNull List<PlotComment> comments,
                @NotNull Map<UUID, Integer> ratings, @Nullable String alias, @Nullable String description) {
        this.id = id;
        this.owner = owner;
        this.trusted = new HashSet<>(trusted);
        this.denied = new HashSet<>(denied);
        this.createdAt = createdAt;
        this.flags = new HashMap<>(flags);
        this.comments = new ArrayList<>(comments);
        this.ratings = new HashMap<>(ratings);
        this.alias = alias;
        this.description = description;
    }
    
    private void initializeDefaultFlags() {
        flags.putIfAbsent("pvp", false);
        flags.putIfAbsent("mob-spawning", true);
        flags.putIfAbsent("animal-spawning", true);
        flags.putIfAbsent("explosions", false);
        flags.putIfAbsent("weather", true);
        flags.putIfAbsent("time-lock", false);
    }
    
    @NotNull
    public PlotId getId() {
        return id;
    }
    
    @Nullable
    public UUID getOwner() {
        return owner;
    }
    
    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }
    
    @NotNull
    public Set<UUID> getTrusted() {
        return new HashSet<>(trusted);
    }
    
    public void addTrusted(@NotNull UUID uuid) {
        trusted.add(uuid);
    }
    
    public void removeTrusted(@NotNull UUID uuid) {
        trusted.remove(uuid);
    }
    
    public boolean isTrusted(@NotNull UUID uuid) {
        return trusted.contains(uuid);
    }
    
    @NotNull
    public Set<UUID> getDenied() {
        return new HashSet<>(denied);
    }
    
    public void addDenied(@NotNull UUID uuid) {
        denied.add(uuid);
    }
    
    public void removeDenied(@NotNull UUID uuid) {
        denied.remove(uuid);
    }
    
    public boolean isDenied(@NotNull UUID uuid) {
        return denied.contains(uuid);
    }
    
    public boolean isOwner(@NotNull UUID uuid) {
        return owner != null && owner.equals(uuid);
    }
    
    public boolean hasAccess(@NotNull UUID uuid) {
        if (isDenied(uuid)) return false;
        return isOwner(uuid) || isTrusted(uuid);
    }
    
    public boolean isClaimed() {
        return owner != null;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    // Flags
    public boolean getFlag(@NotNull String flag) {
        return flags.getOrDefault(flag, false);
    }
    
    public void setFlag(@NotNull String flag, boolean value) {
        flags.put(flag, value);
    }
    
    @NotNull
    public Map<String, Boolean> getFlags() {
        return new HashMap<>(flags);
    }
    
    // Comments
    public void addComment(@NotNull PlotComment comment) {
        comments.add(comment);
    }
    
    @NotNull
    public List<PlotComment> getComments() {
        return new ArrayList<>(comments);
    }
    
    // Ratings
    public void addRating(@NotNull UUID player, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        ratings.put(player, rating);
    }
    
    public boolean hasRated(@NotNull UUID player) {
        return ratings.containsKey(player);
    }
    
    public double getAverageRating() {
        if (ratings.isEmpty()) return 0.0;
        return ratings.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
    
    public int getRatingCount() {
        return ratings.size();
    }
    
    @NotNull
    public Map<UUID, Integer> getRatings() {
        return new HashMap<>(ratings);
    }
    
    // Alias
    @Nullable
    public String getAlias() {
        return alias;
    }
    
    public void setAlias(@Nullable String alias) {
        this.alias = alias;
    }
    
    // Description
    @Nullable
    public String getDescription() {
        return description;
    }
    
    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
