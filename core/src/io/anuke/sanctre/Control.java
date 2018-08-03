package io.anuke.sanctre;

import com.badlogic.gdx.Gdx;
import io.anuke.sanctre.entities.Player;
import io.anuke.sanctre.entities.enemies.Shade;
import io.anuke.sanctre.files.Watcher;
import io.anuke.sanctre.graphics.Shaders;
import io.anuke.sanctre.world.Tile;
import io.anuke.ucore.core.*;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.EntityPhysics;
import io.anuke.ucore.input.Input;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.util.Atlas;

import static io.anuke.sanctre.Vars.*;

public class Control extends Module{
	
	public Control(){
	
		KeyBinds.defaults(
			"up", Input.W,
			"left", Input.A,
			"down", Input.S,
			"right", Input.D,
			"attack1", Input.MOUSE_LEFT,
			"dash", Input.SPACE
		);
		
		Settings.loadAll("io.anuke.sanctre");

        Watcher.watch(Gdx.files.internal("sprites/sprites.atlas"), true, f -> {
			Core.atlas.dispose();
			Core.atlas = new Atlas(f);
			Vars.renderer.reset();
        });

		Watcher.watch(Gdx.files.internal("shaders/distort.fragment"), true, f -> {
			Shaders.distort.reload();
		});

		player = new Player();
		player.add();
		Effects.setShakeFalloff(50000f);
	}

	@Override
	public void init(){
		EntityPhysics.initPhysics(0, 0, world.width() * tilesize, world.height() * tilesize);

		EntityPhysics.collisions().setCollider(tilesize, world::solid, (x, y, rect) -> {
			Tile tile = world.get(x, y);
			if(tile == null)
				rect.setSize(tilesize).setCenter(x * tilesize, y * tilesize);
			else
				tile.wall().getHitbox(tile, rect);
		});

		player.set(world.getStartX()*tilesize, world.getStartY()*tilesize - 40f);

		Shade shade = new Shade();
		shade.set(world.getStartX()*tilesize, world.getStartY()*tilesize);
		shade.add();
	}
	
	@Override
	public void update(){
		if(Inputs.keyTap(Input.ESCAPE))
			Gdx.app.exit();

		if(Inputs.keyTap(Input.P))
			Vars.debug = !Vars.debug;

		Entities.update();

		Timers.update();
		Inputs.update();
	}
}
