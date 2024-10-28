package dev.stjernholm.bitlanbuild.objects;

import java.util.UUID;

public class Vote {

    private final UUID category;
    private final UUID player;
    private int rating;

    public Vote(UUID category, UUID player, int rating) {
        this.category = category;
        this.player = player;
        this.rating = rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public UUID getCategory() {
        return category;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isMatch(UUID category, UUID player) {
        return this.category.equals(category) && this.player.equals(player);
    }
}
