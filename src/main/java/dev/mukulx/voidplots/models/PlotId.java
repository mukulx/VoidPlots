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

import java.util.Objects;

public class PlotId {
    
    private final int x;
    private final int z;
    
    public PlotId(int x, int z) {
        this.x = x;
        this.z = z;
    }
    
    public int getX() {
        return x;
    }
    
    public int getZ() {
        return z;
    }
    
    @NotNull
    public String toString() {
        return x + ";" + z;
    }
    
    @NotNull
    public static PlotId fromString(@NotNull String str) {
        String[] parts = str.split(";");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid plot ID format: " + str);
        }
        return new PlotId(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlotId plotId = (PlotId) o;
        return x == plotId.x && z == plotId.z;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
