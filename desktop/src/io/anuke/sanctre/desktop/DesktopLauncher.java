package io.anuke.sanctre.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.anuke.sanctre.Sanctre;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Sanctre");
		config.setMaximized(true);
		new Lwjgl3Application(new Sanctre(), config);
	}
}
