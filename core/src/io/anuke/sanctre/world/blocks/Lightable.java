package io.anuke.sanctre.world.blocks;

import io.anuke.sanctre.world.Tile;

public interface Lightable{
	
	/**Attempt to light the tile, return true if successful (if the block was not previously lit)*/
	public boolean light(Tile tile);
	
	/**Attempt to extinguish the tile, return true if successful (if the block was previously lit)*/
	public boolean extinguish(Tile tile);
}
