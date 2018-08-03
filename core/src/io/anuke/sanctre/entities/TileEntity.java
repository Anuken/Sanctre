package io.anuke.sanctre.entities;

import io.anuke.sanctre.world.Tile;
import io.anuke.ucore.entities.impl.BaseEntity;

public class TileEntity extends BaseEntity{
    public Tile tile;

    @Override
    public void update() {
        tile.wall().update(tile);
    }
}
