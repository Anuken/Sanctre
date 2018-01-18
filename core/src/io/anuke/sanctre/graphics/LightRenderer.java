package io.anuke.sanctre.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pools;
import io.anuke.sanctre.Vars;
import io.anuke.sanctre.world.Tile;
import io.anuke.sanctre.world.blocks.BlockTypes.Overlay;
import io.anuke.sanctre.world.blocks.BlockTypes.Wall;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.lights.Light;
import io.anuke.ucore.lights.PointLight;
import io.anuke.ucore.lights.RayHandler;
import io.anuke.ucore.util.Mathf;

public class LightRenderer {
    private final int rayamount = 140;
    private final int srayamount = 18;
    private final int chunksize = 3 * Vars.tilesize;
    private RayHandler rays;
    private Color lightColor = Hue.rgb(0.65, 0.5, 0.3).mul(1.1f);
    private Color defLightColor = Color.WHITE;

    private int lastcamx, lastcamy;

    public LightRenderer(){
        rays = new RayHandler();
        rays.setTint(lightColor);
    }

    public void resize(){
        int scl = Core.cameraScale;
        rays.resizeFBO(Gdx.graphics.getWidth()/scl, Gdx.graphics.getHeight()/scl);
        rays.pixelate();
        rays.setBounds(0, 0, Vars.world.width()*Vars.tilesize, Vars.world.height()*Vars.tilesize);
        rays.updateRects();

        lastcamx = lastcamy = 0;
    }

    public void update(){
        OrthographicCamera cam = Core.camera;
        int camcx = Mathf.scl(cam.position.x, chunksize), camcy = Mathf.scl(cam.position.y, chunksize);

        if(camcx != lastcamx || camcy != lastcamy){
            updateRects();
        }

        lastcamx = camcx;
        lastcamy = camcy;
    }

    public void clear(){
        rays.removeAll();
    }

    public void updateRects(){
        OrthographicCamera cam = Core.camera;
        int cwidth = Mathf.scl(cam.viewportWidth, Vars.tilesize) + 1, cheight = Mathf.scl(cam.viewportHeight, Vars.tilesize) + 1;
        int camx = Mathf.scl(cam.position.x, Vars.tilesize), camy = Mathf.scl(cam.position.y, Vars.tilesize);


        for(Rectangle rect : rays.getRects()){
            Pools.free(rect);
        }

        rays.clearRects();

        for(int x = 0; x < cwidth; x ++){
            for(int y = 0; y < cheight; y ++){
                int wx = camx + x - cwidth/2, wy = camy + y - cheight/2;
                Tile tile = Vars.world.get(wx, wy);

                if(tile != null && !tile.passable() && checkSurround(tile)){
                    Rectangle rect = Pools.obtain(Rectangle.class);
                    tile.wall().getHitbox(tile, rect);
                    if(tile.wall() instanceof Wall){
                        rect.y += tile.wall().height;;
                        rect.height = 12f;
                    }
                    if(tile.wall() instanceof Overlay){
                        rect.height -= 3f;
                        rect.y += 5f;
                    }
                    rays.addRect(rect);
                }
            }
        }
        rays.updateRects();
    }

    boolean checkSurround(Tile tile){
        int x = tile.x;
        int y = tile.y;
        return !(
                solid(x+1, y+1) &&
                        solid(x+1, y) &&
                        solid(x+1, y-1) &&
                        solid(x, y+1) &&
                        solid(x, y-1) &&
                        solid(x-1, y+1) &&
                        solid(x-1, y) &&
                        solid(x-1, y-1)
        );
    }

    boolean solid(int x, int y){
        return Vars.world.get(x, y) == null || !Vars.world.get(x, y).passable();
    }

    public Light addLight(float radius){
        return addLight(radius, defLightColor);
    }

    public Light addLight(float radius, Color color){
        Light light = new PointLight(rays, rayamount, color, radius, 0, 0);
        light.setSoftnessLength(15f);
        light.setNoise(4f, 1f, 4f);
        return light;
    }

    public Light addSmallLight(float radius){
        Light light = new PointLight(rays, srayamount, defLightColor, radius, 0, 0);
        //light.setSoftnessLength(10f);
        light.setXray(true);
        return light;
    }

    public void drawLight(){
        rays.setCombinedMatrix(Core.camera);
        rays.updateAndRender();
    }

    public void dispose(){
        rays.dispose();
    }
}
