package io.anuke.sanctre;

import com.badlogic.gdx.Gdx;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.builders.build;
import io.anuke.ucore.scene.builders.button;
import io.anuke.ucore.scene.builders.label;
import io.anuke.ucore.scene.builders.table;
import io.anuke.ucore.scene.ui.KeybindDialog;

public class UI extends SceneModule{
	
	@Override
	public void init(){
		//Gdx.graphics.setVSync(false);
		build.begin();
		new table(){{
			atop().aleft();

			new button("R", () -> new KeybindDialog().show());
			new label(() -> Gdx.graphics.getFramesPerSecond() + " FPS");
		}}.end();
		build.end();
	}
	
}
