package io.anuke.sanctre.world.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import io.anuke.sanctre.Vars;
import io.anuke.sanctre.entities.TileEntity;
import io.anuke.sanctre.world.Block;
import io.anuke.sanctre.world.BlockType;
import io.anuke.sanctre.world.Tile;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.facet.BaseFacet;
import io.anuke.ucore.facet.FacetList;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.facet.SpriteFacet;
import io.anuke.ucore.function.Predicate;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.lights.Light;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

public class BlockTypes{

	public static class Floor extends Block{
		private static TextureRegion temp = new TextureRegion();
		boolean useEdge = true;
		String edgename;

		public Floor(String name) {
			super(name, BlockType.floor);
			edgename = name;
		}

		@Override
		public void draw(FacetList list, Tile tile){
			if(tile.wall() instanceof Wall)
				return;

			if(this != Blocks.air){
				Draw.rect(name + (variants > 0 ? tile.rand(variants) : ""), tile.worldx(), tile.worldy());
			}

			if(useEdge){

				for(GridPoint2 point : Geometry.getD8Points()){
					int dx = point.x, dy = point.y;

					Tile other = Vars.world.get(tile.x + dx, tile.y + dy);

					if(other == null)
						continue;

					Block floor = other.floor();

					if(floor.id <= id || other.wall() instanceof Wall || !(floor instanceof Floor) || !((Floor)floor).useEdge){
						continue;
					}

					if(Math.abs(dx) > 0 && Math.abs(dy) > 0){

						if(Vars.world.get(tile.x + dx, tile.y).floor().id != id ||
								Vars.world.get(tile.x, tile.y + dy).floor().id != id){
							continue;
						}
					}

					TextureRegion region = Draw.region(floor.name + "edge");

					if(region == null)
						region = Draw.region(floor.edge + "edge");

					int tsize = 12;

					int sx = -dx * tsize + 2, sy = -dy * tsize + 2;
					int x = Mathf.clamp(sx, 0, tsize + 4);
					int y = Mathf.clamp(sy, 0, tsize + 4);
					int w = Mathf.clamp(sx + tsize, 0, tsize + 4) - x, h = Mathf.clamp(sy + tsize, 0, tsize + 4) - y;

					float rx = Mathf.clamp(dx * tsize, 0, tsize - w);
					float ry = Mathf.clamp(dy * tsize, 0, tsize - h);

					temp.setTexture(region.getTexture());
					temp.setRegion(region.getRegionX() + x, region.getRegionY() + y + h, w, -h);

					Draw.rect(temp, tile.worldx() - tsize / 2 + rx, tile.worldy() - tsize / 2 + ry, w, h);

				}

			}else{
				int i = -1;

				for(GridPoint2 point : Geometry.getD4Points()){
					Tile other = Vars.world.get(tile.x + point.x, tile.y + point.y);

					i ++;

					if(other == null || other.floor().id == id){
						continue;
					}

					Draw.rect(edgename + "inner", tile.worldx(), tile.worldy(), i*90);

				}

				i = -1;

				for(GridPoint2 point : Geometry.getD8EdgePoints()){
					Tile other = Vars.world.get(tile.x + point.x, tile.y + point.y);

					i ++;

					int overlaps = 0;

					if(Vars.world.get(tile.x + point.x, tile.y).floor().id == id) overlaps ++;
					if(Vars.world.get(tile.x, tile.y + point.y).floor().id == id) overlaps ++;

					if(other == null || other.floor().id == id || overlaps == 1){
						continue;
					}

					Draw.rect(edgename + "inneredge" + (overlaps == 2 ? "2" : ""), tile.worldx(), tile.worldy(), i*90);

				}
			}
		}
	}

	public static class DecalFloor extends Block{
		Block under = null;

		protected DecalFloor(String name) {
			super(name, BlockType.floor);
			variants = 0;
		}

		@Override
		public void draw(FacetList list, Tile tile){
			if(under != null)
				Draw.rect(under.name + (under.variants > 0 ? tile.rand(under.variants) : ""), tile.worldx(), tile.worldy());
			Draw.rect(name + (variants > 0 ? tile.rand(variants) : ""), tile.worldx(), tile.worldy());
		}
	}

	public static class Wall extends Block{
		String edge;
		Predicate<Block> blendWith = block -> block.solid && block instanceof Wall;

		public Wall(String name) {
			super(name, BlockType.wall);
			edge = name;
			variants = 0;
			solid = true;
			hitbox.setSize(12, 18);
			hitbox.y = 3;
		}

		@Override
		public void draw(FacetList list, Tile tile){
			new SpriteFacet(name + (variants > 0 ? tile.rand(variants) : "")).set(tile.worldx(), tile.worldy() - Vars.tilesize / 2f).centerX().addShadow(list, "wallshadow", 6).sort(Sorter.object).add(list);

			drawEdge(list, tile);
		}

		public void drawEdge(FacetList list, Tile tile){
			if(Draw.hasRegion(edge + "edge")){
				new BaseFacet(p -> {
					p.layer = tile.worldy() - Vars.tilesize / 2f - 0.001f;
					float posx = tile.x * Vars.tilesize, posy = tile.y * Vars.tilesize + height;

					int dir = 0;

					for(GridPoint2 point : Geometry.getD4Points()){
						Tile other = Vars.world.get(tile.x + point.x, tile.y + point.y);
						if(other == null || !blendWith.test(other.wall())){
							Draw.rect(edge + "edge", posx, posy, dir * 90);
						}

						dir++;
					}

					dir = 0;

					for(GridPoint2 point : Geometry.getD8EdgePoints()){
						Tile other = Vars.world.get(tile.x + point.x, tile.y + point.y);
						if(other == null || !blendWith.test(other.wall())){
							Draw.rect(edge + "edgecorner", posx, posy, dir * 90);
						}

						dir++;
					}

				}).sort(Sorter.object).add(list);
			}
		}
	}

	public static class Bookshelf extends Wall{
		int shelves = 2;
		int spacing = 6;
		int maxbooks = 10;

		public Bookshelf(String name) {
			super(name);
		}

		@Override
		public void draw(FacetList list, Tile tile){
			new SpriteFacet("wallshadow").set(tile.worldx(), tile.worldy()).center().shadow().add(list);

			new BaseFacet(tile.worldy() - Vars.tilesize / 2, Sorter.object, p -> {
				Draw.grect(name, tile.worldx(), tile.worldy() - Vars.tilesize / 2f);

				if(!Vars.world.isWall(tile.x, tile.y - 1)){

					float startx = tile.worldx() - Vars.tilesize / 2, starty = tile.worldy() - Vars.tilesize / 2;

					for(int shelf = 0; shelf < shelves; shelf++){
						int books = maxbooks - (tile.rand(shelf, 3) - 1);
						int offset = 0;

						if(books < maxbooks && tile.randFloat(1) < 0.5){
							offset = maxbooks - books;
						}

						for(int i = 0; i < books && offset < 9; i++){
							int w = tile.randFloat(7 + i + shelf) < 0.5 ? 2 : 1;
							int h = 4 + tile.rand(7 + i + shelf, 5) / 2 - 1;

							float hue = tile.randFloat(4 + shelf * 2 + i * 5), s = 0.1f + tile.randFloat(5 + shelf * 2 + i * 6) / 2f, b = 0.3f + tile.randFloat(6 + shelf * 2 + i * 7) / 3f;

							Hue.fromHSB(hue, s, b, Tmp.c1);
							Tmp.c1.a = 1f;
							Draw.color(Tmp.c1);
							Draw.crect("blank", startx + 1 + offset, starty + 1 + shelf * spacing, w, h);

							offset += w;
						}
					}

					Draw.color();

				}

			}).add(list);

			drawEdge(list, tile);
		}
	}

	public static class Tree extends Block{

		public Tree(String name) {
			super(name, BlockType.wall);
			hitbox.setSize(12, 2);
		}

		@Override
		public void draw(FacetList list, Tile tile){
			new SpriteFacet(name + (variants > 0 ? tile.rand(variants) : "")).set(tile.worldx(), tile.worldy() - offset).layer(tile.worldy()).centerX().addShadow(list, offset).sort(Sorter.object).add(list);
		}
	}

	public static class Prop extends Block{
		boolean spread = false;
		float spreadrange = 6f;

		public Prop(String name) {
			super(name, BlockType.wall);
			hitbox.setSize(12, 2);
		}

		@Override
		public void draw(FacetList list, Tile tile){
			new SpriteFacet(name).set(tile.worldx() + (int)(spread ? tile.randFloat(0) * spreadrange - spreadrange/2f : 0f),
					tile.worldy() - offset + (int)(spread ? tile.randFloat(1) * spreadrange - spreadrange/2f : 0f))
			.layer(tile.worldy()).centerX()
			.addShadow(list, offset).sort(Sorter.object).add(list);
		}
	}

	public static class Overlay extends Block{
		boolean shadow = true;
		boolean spread = false;
		float spreadrange = 6f;

		public Overlay(String name) {
			super(name, BlockType.wall);
			hitbox.setSize(8, 8);
		}

		@Override
		public void draw(FacetList list, Tile tile){
			String name = this.name + (variants > 0 ? tile.rand(variants) : "");
			SpriteFacet sprite = new SpriteFacet(name)
					.set(tile.worldx() + (int)(spread ? tile.randFloat(0) * spreadrange - spreadrange/2f : 0f),
						tile.worldy() - offset + (int)(spread ? tile.randFloat(1) * spreadrange - spreadrange/2f : 0f))
			.layer(tile.worldy() + 5)
			.center().sort(Sorter.object).sprite();

			if(shadow)
				sprite.addShadow(list, name, offset + 8);

			sprite.add(list);
		}
	}

	public static class WallOverlay extends Block{
		Color color = Color.WHITE.cpy();

		public WallOverlay(String name) {
			super(name, BlockType.wall);
		}

		@Override
		public void draw(FacetList list, Tile tile){
			new SpriteFacet(name + (variants > 0 ? tile.rand(variants) : "")).color(color).set(tile.worldx(), tile.worldy() + Vars.tilesize / 2f - 0.001f).centerX().sort(Sorter.object).add(list);
		}

	}

	public static abstract class SpellCircle extends Block{

		public SpellCircle(String name) {
			super(name, BlockType.wall);
		}

		@Override
		public void draw(FacetList list, Tile tile){
			new BaseFacet(0, Sorter.tile, b -> {
				draw(tile, tile.worldx(), tile.worldy());
			}).add(list);
		}

		public TextureRegion randRune(int index, Tile tile){
			return Vars.renderer.getRune(tile.rand(index, 15) - 1);
		}

		public abstract void draw(Tile tile, float x, float y);
	}

	public static abstract class LiveSpellCircle extends Block{
		SpellShape[] shapes;
		Color color = Color.WHITE;
		float spinspeed = 0f;

		private int cindex = 0;

		public LiveSpellCircle(String name, SpellShape... shapes) {
			super(name, BlockType.wall);
			this.shapes = shapes;
		}

		@Override
		public void draw(FacetList list, Tile tile){
			new BaseFacet(0, Sorter.tile, b -> {
				Draw.colorl(color, 0.9f + Mathf.sin(Timers.time(), 10f, 0.1f));

				int index = 0;

				for(SpellShape shape : shapes){

					float spin = Timers.time() * ((index % 2) - 0.5f)*2f * spinspeed;

					if(shape.runes){
						cindex = 0;

						Angles.circle(shape.sides, f->{
							cindex ++;
							Tmp.t1.trns(f + shape.rotation + spin, shape.radius);
							Draw.rect(randRune(cindex + 1, tile), tile.worldx() + Tmp.t1.x, tile.worldy() + Tmp.t1.y);
						});
					}else{
						Lines.stroke(shape.thickness);
						Lines.poly(shape.sides, tile.worldx(), tile.worldy(), shape.radius, shape.rotation + spin);
					}

					index ++;
				}

				Draw.color();
			}).add(list);
		}

		public TextureRegion randRune(int index, Tile tile){
			return Vars.renderer.getRune(tile.rand(index, 15) - 1);
		}

		static public class SpellShape{
			public int sides;
			public float radius, thickness = 1f, rotation;
			public boolean runes;

			public SpellShape(int sides, float radius, float angle, float thickness){
				this.sides = sides;
				this.radius = radius;
				this.rotation = angle;
				this.thickness = thickness;
			}

			public SpellShape(int sides, float radius, float angle){
				this(sides, radius, angle, 1f);
			}

			public SpellShape(int sides, float radius){
				this(sides, radius, 0);
			}

			public SpellShape(boolean runes, int sides, float radius){
				this(sides, radius, 90f/sides);
				this.runes = runes;
			}
		}
	}

	public static class Checkpoint extends Block{
		public Color darkColor = new Color(0xd5bd7cff), lightColor = new Color(0xffeab2ff);
		public int sides = 3;

		public Checkpoint(String name) {
			super(name, BlockType.wall);
		}

		@Override
		public void draw(FacetList list, Tile tile){

			new SpriteFacet(name).set(tile.worldx(), tile.worldy()).center().sort(Sorter.tile).add(list);

			new BaseFacet(p -> {
				Draw.color(darkColor);
				Lines.poly(tile.worldx(), tile.worldy() + Mathf.sin(Timers.time(), 16f, 2f) + 10f, sides, 4f, Timers.time() / 1f);
				Draw.color(lightColor);
				Lines.poly(tile.worldx(), tile.worldy() + Mathf.sin(Timers.time(), 16f, 2f) + 11f, sides, 4f, Timers.time() / 1f);
				Draw.color();
				p.layer = tile.worldy() - 2;
			}).add(list);
		}
	}

	public static class Spawner extends Block{

		public Spawner(String name) {
			super(name, BlockType.wall);
		}

		@Override
		public void draw(FacetList list, Tile tile){
			//TODO implement
			/*
			if(tile.data1 == -1){
				throw new IllegalArgumentException("Spawner tile detected without any valid spawn data!");
			}

			Spark enemy = new Spark(Prototype.getAllTypes().get(tile.data1));
			enemy.pos().set(tile.worldx(), tile.worldy());
			enemy.add();
			tile.data2 = (short) enemy.getID();
			tile.wall = Blocks.emptySpawner;*/
		}
	}

	public static class Decal extends Block{

		protected Decal(String name) {
			super(name, BlockType.decal);
			variants = 0;
		}

		@Override
		public void draw(FacetList list, Tile tile){
			Draw.rect(name + (variants > 0 ? tile.rand(variants) : ""), tile.worldx(), tile.worldy());
		}
	}

	public abstract static class LightableBlock extends Block implements Lightable{
		boolean lit;
		Block transition;
		float lightradius = 60f;

		protected LightableBlock(String name, boolean lit) {
			super(name, BlockType.wall);
			this.lit = lit;
			update = true;
		}

		public abstract void loadTransition();

		@Override
		public boolean extinguish(Tile tile){
			if(lit){
				if(transition == null){
					throw new RuntimeException("Block transition cannot be null!");
				}
				tile.setBlock(transition);
				return true;
			}else{
				return false;
			}
		}

		@Override
		public boolean light(Tile tile){
			if(!lit){
				if(transition == null){
					throw new RuntimeException("Block transition cannot be null!");
				}
				tile.setBlock(transition);
				return true;
			}else{
				return false;
			}
		}
		
		public void updateLight(FacetList list, Tile tile){
			if(transition == null){
				loadTransition();
			}

			LightEntity entity = tile.entity();

			new BaseFacet(p->{

				if(!lit){
					entity.radius -= Timers.delta()/30f;
					entity.radius = Mathf.clamp(entity.radius);
					return;
				}
				
				Light light = entity.light;
				entity.radius += Timers.delta()/40f;
				entity.radius = Mathf.clamp(entity.radius);
				
				if(!MathUtils.isEqual(light.getDistance(), entity.radius * lightradius))
					light.setDistance(entity.radius * lightradius);
				
				if(!light.isStaticLight())
					light.setStaticLight(true);
				
			}).add(list);
		}

		@Override
		public TileEntity getEntity() {
			return new LightEntity();
		}

		class LightEntity extends TileEntity {
			public Light light;
			public float radius = 0f;

			@Override
			public void added(){
				light = Vars.renderer.lights().addLight(0f);
				light.setPosition(tile.worldx(), tile.worldy());
			}

			@Override
			public void removed(){
				Timers.runFor(lightradius, ()->{
					light.setDistance(Mathf.clamp(light.getDistance() - Timers.delta(), 4f, lightradius));
				}, ()->{
					light.remove();
				});
			}
		}
	}
	
	public abstract static class Torch extends LightableBlock{
		String propname;

		protected Torch(String name, String propname, boolean lit) {
			super(name, lit);
			this.propname = propname;
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			updateLight(list, tile);
			
			new BaseFacet(tile.worldy() - 1f, Sorter.object, p->{
				drawFlame(tile);
			}).add(list);
			
			new SpriteFacet(propname).set(tile.worldx(), tile.worldy() - offset).layer(tile.worldy()).centerX()
			.addShadow(list, offset).sort(Sorter.object).add(list);
		}
		
		void drawFlame(Tile tile){
			LightEntity entity = tile.entity();

			float rad = entity.radius * (Mathf.sin(Timers.time() + tile.randFloat(0)*742, 3f, 0.35f) + 2f);
			
			Draw.color(Color.ORANGE);
			Draw.rect("circle", tile.worldx(), tile.worldy() + rad/3f + height - offset, rad*2, rad*2);
			Draw.color(Color.YELLOW);
			Draw.rect("circle", tile.worldx(), tile.worldy() + height - offset, rad, rad);
			
			Draw.color();
		}
	}
	
	public abstract static class Candle extends LightableBlock{
		int maxcandles = 6;
		int[][] offsets = {
			{3, 3},
			{4, 2},
			{4, 4},
			{3, 4},
			{3, 4},
			{4, 4},
		};
		Color color = Color.valueOf("e1d9d2");
		float h, s, b;
		
		
		public Candle(String name, boolean lit){
			super(name, lit);
			
			float[] colors = Hue.RGBtoHSB(color);
			h = colors[0];
			s = colors[1];
			b = colors[2];
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			updateLight(list, tile);
			
			new BaseFacet(tile.worldy()+12, p->{
				draw(tile, false);
			}).add(list);
			
			new BaseFacet(Sorter.shadow, Sorter.tile, p->{
				draw(tile, true);
			}).add(list);
		}
		
		void draw(Tile tile, boolean shadows){
			int candles = tile.rand(4);
			
			for(int i = 0; i < candles; i ++){
				int dx = tile.rand(i*2 + 0, Vars.tilesize);
				float dy = i * 12f/candles;
				int candle = tile.rand(i*2 + 2, maxcandles);
				
				if(!shadows){
					float hs = 0;//(tile.randFloat(i*2 + 3)-0.5f)/15f;
					float ss = 0;//(tile.randFloat(i*2 + 4)-0.5f)/15f;
					float bs = tile.randFloat(i*2) > 0.5  ? -0.02f : 0.1f;//(tile.randFloat(i*2 + 5)-0.5f)/5f;
					boolean flip = tile.randFloat(i*4 + 5) > 0.5;
					
					flip = false;
					
					Tmp.c1.a = 1f;
					Draw.color(Hue.fromHSB(h + hs, s + ss, b + bs, Tmp.c1));
					Draw.grect("candle" + candle, tile.worldx() + dx - Vars.tilesize/2, tile.worldy() + dy  -Vars.tilesize/2, flip);
					candleDrawn(tile, candle, flip, tile.worldx() + dx - Vars.tilesize/2, tile.worldy() + dy  -Vars.tilesize/2);
				}else{
					Draw.rect("shadow4", tile.worldx() + dx - Vars.tilesize/2, tile.worldy() + dy  -Vars.tilesize/2);
				}
			}
			
			Draw.color();
		}
		

		
		void candleDrawn(Tile tile, int candle, boolean flip, float x, float y){
			LightEntity entity = tile.entity();

			x -= 0.25f;
			y -= 0.75f;
			
			float offsetx = 8f - offsets[candle-1][0];
			float offsety = 8f - offsets[candle-1][1];
			
			if(flip)
				offsetx = 8f-offsetx;
			
			offsety += 1.5f;
			
			float rad = entity.radius * (Mathf.sin(Timers.time() + tile.randFloat((int)(x*742 - y*35))*742, 3f, 0.35f) + 1.4f);
			
			Draw.color(Color.ORANGE);
			Draw.rect("circle", x + offsetx - 4f, y + offsety + rad/3f, rad*2, rad*2);
			Draw.color(Color.YELLOW);
			Draw.rect("circle", x + offsetx - 4f, y + offsety, rad, rad);
			
		}
	}

	public static class Moss extends Decal{
		Color color = new Color();
		boolean rotate = true;

		protected Moss(String name) {
			super(name);
		}

		@Override
		public void draw(FacetList list, Tile tile){

			int i = 0;
			boolean any = false;

			Draw.color(color);

			for(GridPoint2 point : Geometry.getD4Points()){
				
				Tile other = Vars.world.get(point.x + tile.x, point.y + tile.y);

				if(other != null && other.decal() == this){
					int rot = tile.rand(i * 2, 5) - 1;
					Draw.rect(name + "" + (i + (rotate ? rot : 0)) % 4, tile.worldx(), tile.worldy(), rotate ? -rot * 90 : 0);
					any = true;
				}
				i++;
			}

			if(!any){
				Draw.rect(name + "none", tile.worldx(), tile.worldy());
			}

			Draw.color();
		}
	}
}
