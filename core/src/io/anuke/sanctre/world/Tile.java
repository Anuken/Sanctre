package io.anuke.sanctre.world;

import io.anuke.sanctre.Vars;
import io.anuke.sanctre.entities.TileEntity;
import io.anuke.sanctre.world.blocks.Blocks;
import io.anuke.ucore.util.Bits;
import io.anuke.ucore.util.Mathf;

public class Tile{
	private short floor, wall, decal;
	public short x, y;
	public TileEntity entity;
	
	public Tile(int x, int y){
		floor = wall = decal = 0;
		this.x = (short)x;
		this.y = (short)y;
	}
	
	public Tile(int x, int y, Block floor, Block decal, Block wall){
		this(x,y);
		this.floor = floor.id;
		this.wall = wall.id;
		this.decal = decal.id;

		changed();
	}

	public Block wall(){
		return Block.byID(wall);
	}

	public Block floor(){
		return Block.byID(floor);
	}

	public Block decal(){
		return Block.byID(decal);
	}
	
	public boolean passable(){
		return !wall().solid;
	}
	
	public float randFloat(int offset){
		return rand(offset, 200)/200f;
	}
	
	public int rand(int max){
		return rand(0, max);
	}
	
	public int rand(int offset, int max){
		return Mathf.randomSeed(offset + Bits.packLong(x, y), 1, max);
	}
	
	public int worldx(){
		return x* Vars.tilesize;
	}
	
	public int worldy(){
		return y*Vars.tilesize;
	}
	
	public void setBlock(Block selected){
		if(selected.type == BlockType.floor)
			floor = selected.id;
		else if(selected.type == BlockType.decal)
			decal = selected.id;
		else
			wall = selected.id;

		changed();
	}

	public <T extends TileEntity> T entity(){
		return (T)entity;
	}

	private void changed(){
		if(entity != null){
			entity.remove();
			entity = null;
		}

		if(wall().update){
			entity = wall().getEntity();
			entity.tile = this;
			entity.add();
		}
	}
	
	@Override
	public String toString(){
		return "tile{"+floor().name + ", " + wall().name+ "}";
	}

}
