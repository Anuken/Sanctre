package io.anuke.sanctre.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Shader;
import io.anuke.ucore.util.Tmp;

public class Shaders{
	public static final Outline outline = new Outline();
	public static final Glow glow = new Glow();
	public static final Scanline scanLine = new Scanline();
	public static final Distort distort = new Distort();
	public static final PlayerShader player = new PlayerShader();
	
	public static class Outline extends Shader{
		public Color color = new Color();
		
		public Outline(){
			super("outline", "outline");
		}
		
		@Override
		public void apply(){
			shader.setUniformf("u_color", color);
			shader.setUniformf("u_texsize", Tmp.v1.set(region.getTexture().getWidth(), region.getTexture().getHeight()));
		}
	}

	public static class PlayerShader extends Shader{
		public Color color = new Color();
		public Color light = new Color();
		public Color dark = new Color();
		public Color skin = new Color();
		public float hittime = 0f;

		public PlayerShader(){
			super("player", "outline");
		}

		@Override
		public void apply(){
			shader.setUniformf("u_hitcolor", color);
			shader.setUniformf("u_hittime", hittime);
            shader.setUniformf("u_light", light);
            shader.setUniformf("u_dark", dark);
            shader.setUniformf("u_skin", skin);
		}
	}
	
	public static class Scanline extends Shader{
		public float screenx, screeny, time;
		
		public Scanline(){
			super("scanline", "outline");
		}
		
		public void set(float screenx, float screeny){
			this.screenx = screenx;
			this.screeny = screeny;
		}

		@Override
		public void apply(){
			Core.camera.project(Tmp.v31.set(screenx, screeny, 0));
			shader.setUniformf("time", Timers.time());
			shader.setUniformf("resolution", (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
			shader.setUniformf("pos", Tmp.v31.x/Gdx.graphics.getWidth(), Tmp.v31.y/Gdx.graphics.getHeight());
		}
	}
	
	public static class Glow extends Shader{
		public float screenx, screeny, time;
		public Color color = Color.WHITE.cpy();
		
		public Glow(){
			super("glow", "outline");
		}
		
		public void set(float screenx, float screeny, Color color){
			this.screenx = screenx;
			this.screeny = screeny;
			this.color = color;
		}

		@Override
		public void apply(){
			Core.camera.project(Tmp.v31.set(screenx, screeny, 0));
			shader.setUniformf("time", Timers.time());
			shader.setUniformf("glowcolor", color);
			shader.setUniformf("bounds", 0.033f);
			shader.setUniformf("resolution", (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
			shader.setUniformf("pos", Tmp.v31.x/Gdx.graphics.getWidth(), Tmp.v31.y/Gdx.graphics.getHeight());
		}
	}
	
	public static class Distort extends Shader{
		public float offsetx, offsety, hittime;
		public Color hitcolor = new Color();
		
		public Distort(){
			super("distort", "outline");
		}
		
		@Override
		public void apply(){
			Core.camera.project(Tmp.v31.set(offsetx, offsety, 0));
			shader.setUniformf("u_hittime", hittime);
			shader.setUniformf("u_time", Timers.time());
			shader.setUniformf("u_hitcolor", hitcolor);
			shader.setUniformf("u_offset", Tmp.v31.x/Gdx.graphics.getWidth(), Tmp.v31.y/Gdx.graphics.getHeight());
			shader.setUniformf("u_resolution", Tmp.v2.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
			shader.setUniformf("u_texsize", Tmp.v1.set(region.getTexture().getWidth(), region.getTexture().getHeight()));
		}
		
	}
}
