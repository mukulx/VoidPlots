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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Plot Model Tests")
class PlotTest {
    
    private PlotId plotId;
    private UUID ownerUuid;
    private UUID trustedUuid;
    private UUID deniedUuid;
    private Plot plot;
    
    @BeforeEach
    void setUp() {
        plotId = new PlotId(0, 0);
        ownerUuid = UUID.randomUUID();
        trustedUuid = UUID.randomUUID();
        deniedUuid = UUID.randomUUID();
        plot = new Plot(plotId, ownerUuid);
    }
    
    @Nested
    @DisplayName("Ownership Tests")
    class OwnershipTests {
        
        @Test
        @DisplayName("Should identify owner correctly")
        void testIsOwner() {
            assertTrue(plot.isOwner(ownerUuid));
            assertFalse(plot.isOwner(trustedUuid));
        }
        
        @Test
        @DisplayName("Should return correct owner UUID")
        void testGetOwner() {
            assertEquals(ownerUuid, plot.getOwner());
        }
        
        @Test
        @DisplayName("Should allow changing owner")
        void testSetOwner() {
            UUID newOwner = UUID.randomUUID();
            plot.setOwner(newOwner);
            assertEquals(newOwner, plot.getOwner());
            assertTrue(plot.isOwner(newOwner));
        }
        
        @Test
        @DisplayName("Should handle unclaimed plots")
        void testUnclaimedPlot() {
            Plot unclaimedPlot = new Plot(plotId, null);
            assertFalse(unclaimedPlot.isClaimed());
            assertNull(unclaimedPlot.getOwner());
        }
    }
    
    @Nested
    @DisplayName("Trust System Tests")
    class TrustTests {
        
        @Test
        @DisplayName("Should add trusted player")
        void testAddTrusted() {
            plot.addTrusted(trustedUuid);
            assertTrue(plot.isTrusted(trustedUuid));
        }
        
        @Test
        @DisplayName("Should remove trusted player")
        void testRemoveTrusted() {
            plot.addTrusted(trustedUuid);
            plot.removeTrusted(trustedUuid);
            assertFalse(plot.isTrusted(trustedUuid));
        }
        
        @Test
        @DisplayName("Should return trusted players set")
        void testGetTrusted() {
            plot.addTrusted(trustedUuid);
            assertTrue(plot.getTrusted().contains(trustedUuid));
            assertEquals(1, plot.getTrusted().size());
        }
    }
    
    @Nested
    @DisplayName("Ban System Tests")
    class BanTests {
        
        @Test
        @DisplayName("Should add denied player")
        void testAddDenied() {
            plot.addDenied(deniedUuid);
            assertTrue(plot.isDenied(deniedUuid));
        }
        
        @Test
        @DisplayName("Should remove denied player")
        void testRemoveDenied() {
            plot.addDenied(deniedUuid);
            plot.removeDenied(deniedUuid);
            assertFalse(plot.isDenied(deniedUuid));
        }
        
        @Test
        @DisplayName("Denied player should not have access")
        void testDeniedNoAccess() {
            plot.addDenied(deniedUuid);
            assertFalse(plot.hasAccess(deniedUuid));
        }
    }
    
    @Nested
    @DisplayName("Access Control Tests")
    class AccessTests {
        
        @Test
        @DisplayName("Owner should have access")
        void testOwnerHasAccess() {
            assertTrue(plot.hasAccess(ownerUuid));
        }
        
        @Test
        @DisplayName("Trusted player should have access")
        void testTrustedHasAccess() {
            plot.addTrusted(trustedUuid);
            assertTrue(plot.hasAccess(trustedUuid));
        }
        
        @Test
        @DisplayName("Denied player should not have access even if trusted")
        void testDeniedOverridesTrust() {
            plot.addTrusted(trustedUuid);
            plot.addDenied(trustedUuid);
            assertFalse(plot.hasAccess(trustedUuid));
        }
        
        @Test
        @DisplayName("Random player should not have access")
        void testRandomPlayerNoAccess() {
            UUID randomUuid = UUID.randomUUID();
            assertFalse(plot.hasAccess(randomUuid));
        }
    }
    
    @Nested
    @DisplayName("Flag System Tests")
    class FlagTests {
        
        @Test
        @DisplayName("Should set and get flags")
        void testSetGetFlag() {
            plot.setFlag("pvp", true);
            assertTrue(plot.getFlag("pvp"));
        }
        
        @Test
        @DisplayName("Should return false for unset flags")
        void testUnsetFlag() {
            assertFalse(plot.getFlag("nonexistent"));
        }
        
        @Test
        @DisplayName("Should initialize default flags")
        void testDefaultFlags() {
            assertFalse(plot.getFlag("pvp"));
            assertTrue(plot.getFlag("mob-spawning"));
            assertTrue(plot.getFlag("animal-spawning"));
            assertFalse(plot.getFlag("explosions"));
        }
    }
    
    @Nested
    @DisplayName("Rating System Tests")
    class RatingTests {
        
        @Test
        @DisplayName("Should add rating")
        void testAddRating() {
            UUID raterUuid = UUID.randomUUID();
            plot.addRating(raterUuid, 5);
            assertTrue(plot.hasRated(raterUuid));
        }
        
        @Test
        @DisplayName("Should calculate average rating")
        void testAverageRating() {
            plot.addRating(UUID.randomUUID(), 5);
            plot.addRating(UUID.randomUUID(), 3);
            assertEquals(4.0, plot.getAverageRating(), 0.01);
        }
        
        @Test
        @DisplayName("Should return zero for no ratings")
        void testNoRatings() {
            assertEquals(0.0, plot.getAverageRating());
            assertEquals(0, plot.getRatingCount());
        }
        
        @Test
        @DisplayName("Should throw exception for invalid rating")
        void testInvalidRating() {
            UUID raterUuid = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class, () -> plot.addRating(raterUuid, 0));
            assertThrows(IllegalArgumentException.class, () -> plot.addRating(raterUuid, 6));
        }
    }
    
    @Nested
    @DisplayName("Comment System Tests")
    class CommentTests {
        
        @Test
        @DisplayName("Should add comment")
        void testAddComment() {
            PlotComment comment = new PlotComment(
                UUID.randomUUID(),
                "TestPlayer",
                "Great plot!",
                System.currentTimeMillis()
            );
            plot.addComment(comment);
            assertEquals(1, plot.getComments().size());
            assertTrue(plot.getComments().contains(comment));
        }
        
        @Test
        @DisplayName("Should return empty list for no comments")
        void testNoComments() {
            assertTrue(plot.getComments().isEmpty());
        }
    }
}
