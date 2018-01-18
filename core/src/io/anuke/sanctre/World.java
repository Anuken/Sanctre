package io.anuke.sanctre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import io.anuke.sanctre.files.Watcher;
import io.anuke.sanctre.world.Block;
import io.anuke.sanctre.world.MapLoader;
import io.anuke.sanctre.world.Tile;
import io.anuke.sanctre.world.blocks.BlockTypes.Wall;
import io.anuke.sanctre.world.blocks.Blocks;
import io.anuke.ucore.UCore;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.util.Mathf;

public class World extends Module{
	private Tile[][] tiles;
	private int width = 512, height = 512;
	private int startx, starty;
	private MapLoader loader = new MapLoader();

	public World(){
		loadMap("arena");

		Watcher.watch(Gdx.files.internal("maps/arena.tmx"), true, file -> {
			UCore.log("changed");
			loadMap(file.nameWithoutExtension());
			Vars.renderer.lights().clear();
			Vars.renderer.reset();
		});
	}

	public void loadMap(String name){
		TiledMap map = loader.load("maps/"+name + ".tmx");
		TiledMapTileLayer floor = (TiledMapTileLayer)map.getLayers().get("floor");
		TiledMapTileLayer decals = (TiledMapTileLayer)map.getLayers().get("decals");
		TiledMapTileLayer walls = (TiledMapTileLayer)map.getLayers().get("walls");

		width = floor.getWidth();
		height = floor.getHeight();

		tiles = new Tile[width][height];

		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				Cell fc = floor.getCell(x, y);
				Cell dc = decals.getCell(x, y);
				Cell wc = walls.getCell(x, y);

				Block floorblock = fc == null ? Blocks.air :
						Block.byName((String)fc.getTile().getProperties().get("name"));

				Block wallblock = wc == null ? Blocks.air :
						Block.byName((String)wc.getTile().getProperties().get("name"));

				Block decalblock = dc == null ? Blocks.air :
						Block.byName((String)dc.getTile().getProperties().get("name"));

				if(wallblock == Blocks.spawncircle){
					startx = x;
					starty = y;
				}

				tiles[x][y] = new Tile(x, y, floorblock, decalblock, wallblock);
			}
		}
	}

	public int width(){
		return width;
	}

	public int height(){
		return height;
	}
	
	public int getStartX(){
		return startx;
	}
	
	public int getStartY(){
		return starty;
	}

	public Tile[][] getTiles(){
		return tiles;
	}

	/** Returns null when out of bounds. Take care. */
	public Tile get(int x, int y){
		if(!Mathf.inBounds(x, y, tiles)){
			return null;
		}

		return tiles[x][y];
	}
	
	public Tile getWorld(float x, float y){
		return get(Mathf.scl2(x, Vars.tilesize), Mathf.scl2(y, Vars.tilesize));
	}

	public void set(int x, int y, Tile tile){
		tile.x = (short)x;
		tile.y = (short)y;
		tiles[x][y] = tile;
	}
	
	public boolean isWall(int x, int y){
		Tile tile = get(x, y);
		return tile != null && tile.wall() instanceof Wall;
	}
	
	public boolean solid(int x, int y){
		Tile tile = get(x, y);
		return tile == null || !tile.passable();
	}
}
