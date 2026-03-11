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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlotComment Model Tests")
class PlotCommentTest {
    
    @Test
    @DisplayName("Should create comment with all fields")
    void testCreateComment() {
        UUID authorUuid = UUID.randomUUID();
        String authorName = "TestPlayer";
        String message = "Great plot!";
        long timestamp = System.currentTimeMillis();
        
        PlotComment comment = new PlotComment(authorUuid, authorName, message, timestamp);
        
        assertEquals(authorUuid, comment.getAuthorUuid());
        assertEquals(authorName, comment.getAuthorName());
        assertEquals(message, comment.getMessage());
        assertEquals(timestamp, comment.getTimestamp());
    }
    
    @Test
    @DisplayName("Should preserve message content")
    void testMessageContent() {
        String longMessage = "This is a longer comment with special characters: !@#$%^&*()";
        PlotComment comment = new PlotComment(
            UUID.randomUUID(),
            "Player",
            longMessage,
            System.currentTimeMillis()
        );
        
        assertEquals(longMessage, comment.getMessage());
    }
}
