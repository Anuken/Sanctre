package io.anuke.sanctre.world.blocks;

import com.badlogic.gdx.graphics.Color;
import io.anuke.sanctre.Vars;
import io.anuke.sanctre.world.Block;
import io.anuke.sanctre.world.BlockType;
import io.anuke.sanctre.world.Tile;
import io.anuke.sanctre.world.blocks.BlockTypes.*;
import io.anuke.sanctre.world.blocks.BlockTypes.LiveSpellCircle.SpellShape;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.facet.BaseFacet;
import io.anuke.ucore.facet.FacetList;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.facet.SpriteFacet;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;

public class Blocks{
	public static final Block
	
	air = new Block("air", BlockType.floor){
		
	},
	
	grass = new Floor("grass"){{
		variants = 0;
	}},
	pgrass = new Floor("pgrass"){{
		edgecolor = Hue.rgb(0x63569bff, 0.65f);
	}}, 
	pgrassdk = new Floor("pgrassdk"){{
		edgecolor = Hue.rgb(0x5a4e8cff, 0.65f);
	}}, 
	psoil = new Floor("psoil"){{
		edgecolor = Hue.rgb(0x5a4e8cff, 0.6f);
	}}, 
	marble = new Floor("marble"){{
		variants = 4;
		edgecolor = Hue.rgb(0x7f788cff, 0.7f);
	}}, 
	marbles = new Floor("marbles"){{
		variants = 0;
	}}, 
	marbles2 = new Floor("marbles2"){{
		variants = 0;
	}}, 
	blocks = new Overlay("blocks"),
	checkpoint = new Checkpoint("respawnpoint"),
	startcheckpoint = new Checkpoint("startpoint"){{
		sides = 4;
		darkColor = Color.valueOf("dc997e");
		lightColor = Color.valueOf("ffb294");
	}},
	spawner = new Spawner("spawner"),
	pwall = new Wall("pwall"),
	pwall2 = new Wall("pwall2"),
	pwall3 = new Wall("pwall3"),
	pwall4 = new Wall("pwall4"),
	
	marblepillar = new Wall("marblepillar"),
	marbleblock = new Wall("marbleblock"),
	marker = new Prop("marker"){{
		offset = 1;
	}},
	bluetree = new Tree("bluetree"){{
		offset = 4;
		variants = 2;
	}},
	bluesapling = new Tree("bluesapling"){{
		offset = 1;
		variants = 3;
	}},
	cavewall = new Wall("cavewall"){{
		variants = 0;
		height = 13;
		blendWith = block->block.name.contains("cave") && block instanceof Wall;
	}},
	brickwall = new Wall("brickwall"){{
		variants = 3;
		height = 13;
		blendWith = block->block.name.contains("brick") && block instanceof Wall;
	}},
	tallbrickwall = new Wall("tallbrickwall"){{
		height = 19;
		edge = "brickwall";
		blendWith = block->block.name.contains("brick") && block instanceof Wall;
	}},
	brickshelf = new Wall("brickshelf"){{
		height = 13;
		variants = 3;
		edge = "brickwall";
		blendWith = block->block.name.contains("brick") && block instanceof Wall;
	}},
	stonefloor = new Floor("stonefloor"){{
		variants = 5;
	}},
	cavefloor = new Floor("cavefloor"){{
		variants = 1;
	}},
	bottles = new Overlay("bottles"){
		Color color = new Color(0.9f, 0.9f, 0.9f, 0.86f);
		{
			variants = 5;
			shadow = false;
			spread = true;
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			String name = this.name + (variants > 0 ? tile.rand(variants) : "");
			new SpriteFacet(name).set(tile.worldx(), tile.worldy()-offset)
			.color(color)
			.layer(tile.worldy())
			.center().sort(Sorter.object).add(list);
			
			new SpriteFacet(name + "shadow").set(tile.worldx(), tile.worldy()-offset)
				.layer(tile.worldy()+5)
				.center().shadow().add(list);
		}
	},
	barrel = new Prop("barrel"){{
		offset = 3;
		spread = true;
	}},
	brokenbarrel = new Prop("brokenbarrel"){{
		offset = 3;
		spread = true;
	}},
	torchlit = new Torch("torchstandlit", "torchstand", true){
		{
			offset = 1;
			height -= 1f;
			lightradius = 200f;
		}

		@Override
		public void loadTransition(){
			transition = torchunlit;
		}
	},
	torchunlit = new Torch("torchstand", "torchstand", false){
		{
			offset = 1;
			height -= 1f;
		}

		@Override
		public void loadTransition(){
			transition = torchlit;
		}
	},
	books = new Overlay("books"){
		Color[] colors = {Color.valueOf("4c5f3e"), Color.valueOf("7b6844"), Color.valueOf("445e6d"), Color.valueOf("704533"), Color.valueOf("8f875f")};
		Color temp = new Color();
		int w = 3, h = 4;
		
		@Override
		public void draw(FacetList list, Tile tile){
			new BaseFacet(tile.worldy()+5, p->{
				
				int amount = tile.rand(-1, colors.length);
				
				for(int i = 0; i < amount; i ++){
					
					int dx = tile.rand(i*2, Vars.tilesize);
					int dy = tile.rand(i*2 + 1, Vars.tilesize);
					int rot = tile.rand(i*2 + 2, 360);
					float mul = 1f + (tile.rand(i*2 + 3, 255)/255f-0.5f)/6f;
					
					drawBook(w, h, 
							tile.worldx() + dx-Vars.tilesize/2,
							tile.worldy() + dy - Vars.tilesize/2,
							rot, colors[i], mul);
				}
			}).add(list);
		}
	},
	candles = new Candle("candles", false){

		@Override
		public void loadTransition(){
			transition = litcandles;
		}
	},
	litcandles = new Candle("litcandles", true){

		@Override
		public void loadTransition(){
			transition = candles;
		}
	},
	rocks = new Overlay("rocks"),
	bigrock = new Overlay("bigrocks"){{
		variants = 0;
		solid = true;
		hitbox.setSize(10, 10);
		destructible = true;
		destoyDamage = 12;
		destroyBlock = Blocks.rocks;
		destroyParticle = "rockbreak";
		hitParticle = "rockspark";
	}},
	rubble = new Overlay("rubble"){{
		variants = 0;
		offset = -1;
	}},
	redcarpet = new Floor("redcarpet"){{
		useEdge = false;
		variants = 0;
	}},
	tatteredredcarpet = new DecalFloor("tatteredredcarpet"){{
		variants = 3;
		under = stonefloor;
	}},
	tatteredredcarpetedge = new DecalFloor("tatteredredcarpetedge"){{
		under = stonefloor;
	}},
	tatteredredcarpetedge2 = new DecalFloor("tatteredredcarpetedge2"){{
		under = stonefloor;
	}},
	tatteredredcarpetedge3 = new DecalFloor("tatteredredcarpetedge3"){{
		under = stonefloor;
	}},
	tatteredredcarpetedge4 = new DecalFloor("tatteredredcarpetedge4"){{
		under = stonefloor;
	}},
	tatteredredcarpetmid = new DecalFloor("tatteredredcarpetmid"){{
		under = stonefloor;
	}},
	tatteredredcarpetl = new DecalFloor("tatteredredcarpetl"){{
		under = stonefloor;
	}},
	tatteredredcarpetr = new DecalFloor("tatteredredcarpetr"){{
		under = stonefloor;
	}},
	tatteredredcarpett = new DecalFloor("tatteredredcarpett"){{
		under = stonefloor;
	}},
	tatteredredcarpetb = new DecalFloor("tatteredredcarpetb"){{
		under = stonefloor;
	}},
	tatteredredcarpetl2 = new DecalFloor("tatteredredcarpetl2"){{
		under = stonefloor;
	}},
	tatteredredcarpetr2 = new DecalFloor("tatteredredcarpetr2"){{
		under = stonefloor;
	}},
	redcarpettriml = new Decal("redcarpettriml"){{
		variants = 2;
	}},
	redcarpettrimr = new Decal("redcarpettrimr"){{
		variants = 2;
	}},
	redcarpettrimt = new Decal("redcarpettrimt"){{
		variants = 2;
	}},
	redcarpettrimb = new Decal("redcarpettrimb"){{
		variants = 2;
	}},
	table = new Prop("table"){{
		offset = 3;
	}},
	booktable = new Prop("booktable"){
		Color[] colors = {Color.valueOf("4c5f3e"), Color.valueOf("7b6844"), Color.valueOf("445e6d"), Color.valueOf("704533"), Color.valueOf("8f875f")};
		
		{
			offset = 3;
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			table.draw(list, tile);
			new BaseFacet(tile.worldy() - offset, p->{
				
				int amount = tile.rand(-1, 3);
				
				for(int i = 0; i < amount; i ++){
					
					int index = tile.rand(-2 - i*2, colors.length) - 1;
					int dx = tile.rand(i*2 + 0, 14);
					int dy = tile.rand(i*2 + 2, 4);
					int rot = tile.rand(i*2 + 4, 360);
					float mul = 1f + (tile.rand(i*2 + 8, 255)/255f-0.5f)/5f;
					
					drawBook(3, 4, 
							tile.worldx() + dx-7, 
							tile.worldy() + dy + 7 - 2 - offset,
							rot, colors[index], mul);
				}
			}).add(list);
		}
	},
	smallshelf = new Bookshelf("smallshelf"){{
		blendWith = block->block == this;
		height = 13;
		edge = "shelf";
	}},
	tallshelf = new Bookshelf("tallshelf"){{
		blendWith = block->block == this;
		height = 19;
		shelves = 3;
		hitbox.height = 25;
		hitbox.y += 3.5f;
		edge = "shelf";
	}},
	verytallshelf = new Bookshelf("verytallshelf"){{
		blendWith = block->block == this || block == verytallshelfblank;
		height = 25;
		shelves = 4;
		edge = "shelf";
		hitbox.height  += 12f;
		hitbox.y += 6f;
	}},
	verytallshelfblank = new Wall("verytallshelf-blank"){{
		blendWith = block->block == this || block == verytallshelf;
		height = 25;
		edge = "shelf";
	}},
	cobweb = new WallOverlay("cobweb"){{
		variants = 0;
	}},
	moss = new Moss("moss"){
		{
			color = Color.valueOf("515c14");
		}
	},
	thickmoss = new Moss("thickmoss"){
		{
			color = Color.valueOf("5f6639");
		}
	},
	blackweb = new Moss("blackweb"){
		{
			color = Color.valueOf("414141");
		}
	},
	blackink = new Moss("blackink"){
		{
			color = Color.valueOf("414141");
		}
	},
	wallmoss = new WallOverlay("wallmoss"){
		{
			color = Color.valueOf("66741b");
		}
	},
	spawncircle = new SpellCircle("spawncircle"){
		
		@Override
		public void draw(Tile tile, float x, float y){
			
			Draw.color(Color.DARK_GRAY);
			Lines.stroke(2f);
			
			Draw.rect("spawncircle", x, y);
			
			Angles.circleVectors(16, 40, (ox, oy)->{
				Draw.rect(randRune((int)(ox+oy*100), tile), (int)(x + ox), (int)(y + oy));
			});
			
			Draw.reset();
		}
	},
	
	lightspawncircle = new LiveSpellCircle("lightspawncircle",
		new SpellShape(25, 26f, 0f, 2f),
		new SpellShape(5, 25f, 0f, 1f),
		new SpellShape(5, 25f, 180f/5, 1f),
		new SpellShape(true, 10, 13f)
	){
		
	},
	
	end = null
	;
	
	static Color temp = new Color();
	
	static void drawBook(int w, int h, float x, float y, float rot, Color color, float mul){
		
		temp.set(color).mul(mul, mul, mul, 1f);
		
		Draw.color(0f, 0f, 0f, 0.16f);
		Draw.rect("blank", x, y - 2f, w, h, rot);
		
		Draw.color(temp.mul(0.8f, 0.8f, 0.8f, 1f));
		Draw.rect("blank", x, y - 0.7f, w, h, rot);
		
		//whiteness
		//Draw.color(Hue.lightness(0.7f));
		//Draw.rect("blank", x, y - 1f, w, h, rot);
		
		Draw.color(temp.set(color).mul(mul, mul, mul, 1f));
		Draw.rect("blank", x, y, w, h, rot);
		
		Draw.color();
	}
}
