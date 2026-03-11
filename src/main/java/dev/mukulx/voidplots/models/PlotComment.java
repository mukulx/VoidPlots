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

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlotComment {
    
    private final UUID authorUuid;
    private final String authorName;
    private final String message;
    private final long timestamp;
    
    public PlotComment(@NotNull UUID authorUuid, @NotNull String authorName, @NotNull String message, long timestamp) {
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    @NotNull
    public UUID getAuthorUuid() {
        return authorUuid;
    }
    
    @NotNull
    public String getAuthorName() {
        return authorName;
    }
    
    @NotNull
    public String getMessage() {
        return message;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
