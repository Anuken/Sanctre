package io.anuke.sanctre.entities;

public enum Direction {
    front("front", false),
    back("back", false),
    left("side", true),
    right("side", false);

    public final boolean flipped;
    public final String texture;

    Direction(String texture, boolean flipped){
        this.texture = texture;
        this.flipped = flipped;
    }
}
