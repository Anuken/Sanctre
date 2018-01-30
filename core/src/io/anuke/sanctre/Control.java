package io.anuke.sanctre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import io.anuke.sanctre.entities.Player;
import io.anuke.sanctre.entities.enemies.Shade;
import io.anuke.sanctre.files.Watcher;
import io.anuke.sanctre.graphics.Shaders;
import io.anuke.sanctre.world.Tile;
import io.anuke.ucore.core.*;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.util.Atlas;
import io.anuke.ucore.util.Input;
import io.anuke.ucore.util.Log;

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

		player = new Player().add();
		Effects.setShakeFalloff(50000f);
	}

	@Override
	public void init(){
		Entities.initPhysics(0, 0, world.width() * tilesize, world.height() * tilesize);

		Entities.collisions().setCollider(tilesize, world::solid, (x, y, rect) -> {
			Tile tile = world.get(x, y);
			if(tile == null)
				rect.setSize(tilesize).setCenter(x * tilesize, y * tilesize);
			else
				tile.wall().getHitbox(tile, rect);
		});

		player.set(world.getStartX()*tilesize, world.getStartY()*tilesize - 40f);

		new Shade().set(world.getStartX()*tilesize, world.getStartY()*tilesize).add();
	}
	
	@Override
	public void update(){
		if(Inputs.keyTap(Keys.ESCAPE))
			Gdx.app.exit();

		if(Inputs.keyTap(Keys.P))
			Vars.debug = !Vars.debug;

		Entities.update();

		Timers.update();
		Inputs.update();
	}
}
