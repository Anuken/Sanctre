package io.anuke.sanctre.entities;

import io.anuke.sanctre.world.Tile;
import io.anuke.ucore.entities.Entity;

public class TileEntity extends Entity {
    public Tile tile;

    @Override
    public void update() {
        tile.wall().update(tile);
    }
}
