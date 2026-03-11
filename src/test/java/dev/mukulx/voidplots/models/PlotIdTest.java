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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlotId Model Tests")
class PlotIdTest {
    
    @Test
    @DisplayName("Should create PlotId with coordinates")
    void testCreatePlotId() {
        PlotId plotId = new PlotId(5, 10);
        assertEquals(5, plotId.getX());
        assertEquals(10, plotId.getZ());
    }
    
    @Test
    @DisplayName("Should convert to string correctly")
    void testToString() {
        PlotId plotId = new PlotId(5, 10);
        assertEquals("5;10", plotId.toString());
    }
    
    @Test
    @DisplayName("Should parse from string correctly")
    void testFromString() {
        PlotId plotId = PlotId.fromString("5;10");
        assertEquals(5, plotId.getX());
        assertEquals(10, plotId.getZ());
    }
    
    @Test
    @DisplayName("Should handle negative coordinates")
    void testNegativeCoordinates() {
        PlotId plotId = new PlotId(-5, -10);
        assertEquals(-5, plotId.getX());
        assertEquals(-10, plotId.getZ());
        assertEquals("-5;-10", plotId.toString());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid string format")
    void testInvalidStringFormat() {
        assertThrows(IllegalArgumentException.class, () -> PlotId.fromString("invalid"));
        assertThrows(IllegalArgumentException.class, () -> PlotId.fromString("5"));
        assertThrows(NumberFormatException.class, () -> PlotId.fromString("a;b"));
    }
    
    @Test
    @DisplayName("Should implement equals correctly")
    void testEquals() {
        PlotId plotId1 = new PlotId(5, 10);
        PlotId plotId2 = new PlotId(5, 10);
        PlotId plotId3 = new PlotId(6, 10);
        
        assertEquals(plotId1, plotId2);
        assertNotEquals(plotId1, plotId3);
        assertNotEquals(plotId1, null);
        assertNotEquals(plotId1, "5;10");
    }
    
    @Test
    @DisplayName("Should implement hashCode correctly")
    void testHashCode() {
        PlotId plotId1 = new PlotId(5, 10);
        PlotId plotId2 = new PlotId(5, 10);
        
        assertEquals(plotId1.hashCode(), plotId2.hashCode());
    }
}
