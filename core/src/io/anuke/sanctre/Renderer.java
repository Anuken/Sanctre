package io.anuke.sanctre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.anuke.sanctre.entities.FacetEffect;
import io.anuke.sanctre.graphics.BlockRenderer;
import io.anuke.sanctre.graphics.DecalRenderer;
import io.anuke.sanctre.graphics.LightRenderer;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.facet.FacetLayerHandler;
import io.anuke.ucore.facet.Facets;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.graphics.Surface;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.util.Tmp;

import static io.anuke.ucore.core.Core.atlas;
import static io.anuke.ucore.core.Core.cameraScale;

public class Renderer extends RendererModule {
    BlockRenderer blocks;
    LightRenderer lights;
    DecalRenderer decals;

    public Surface darkSurface;

    public Renderer(){
        atlas = new Atlas("sprites.atlas");
        blocks = new BlockRenderer();
        lights = new LightRenderer();
        decals = new DecalRenderer();

        cameraScale = 3;
        pixelate();

        FacetLayerHandler hand = new FacetLayerHandler();
        darkSurface = hand.allDrawLayers.peek().surface;

        Facets.instance().setLayerManager(hand);

        Effects.setEffectProvider((effect, color, x, y, rotation) -> new FacetEffect(effect, color, rotation).set(x, y).add());
    }

    public LightRenderer lights() {
        return lights;
    }

    @Override
    public void update(){
        lights.update();

        setCamera((int)Vars.player.x, (int)Vars.player.y);
        updateShake();
        clampCamera(0f, 0f, Vars.world.width() * Vars.tilesize, Vars.world.height() * Vars.tilesize);
        drawDefault();

        lights.drawLight();

        Graphics.setScreen();
        Core.batch.draw(darkSurface.texture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        Graphics.end();

        if(Vars.debug){
            Graphics.beginCam();
            drawHitboxes();
            Graphics.end();
        }

        record();
    }

    @Override
    public void draw(){
        Graphics.surface(darkSurface);
        Graphics.surface();

        blocks.drawFloor();
        decals.draw();
        blocks.drawBlocks();

        Facets.instance().renderAll();
    }

    @Override
    public void resize(){
        lights.resize();
    }

    public DecalRenderer decals() {
        return decals;
    }

    private void drawHitboxes(){
        for(Entity entity : Entities.defaultGroup().all()){
            if(!(entity instanceof SolidEntity)) continue;

            SolidEntity s = (SolidEntity)entity;
            Rectangle r = s.hitbox.getRect(s.x, s.y);
            Draw.color(Color.GREEN);
            Draw.linerect(r);

            r = s.hitboxTile.getRect(s.x, s.y);
            Draw.color(Color.ORANGE);
            Draw.linerect(r);
        }

        Draw.reset();
    }

    public void reset(){
        resetFloor();
        resetWalls();
        lights.updateRects();
    }

    public void resetWalls(){
        blocks.resetWalls();
    }

    public void resetFloor(){
        blocks.recache();
    }

    public TextureRegion getRune(int index){
        TextureRegion region = Tmp.tr1;
        region.setRegion(Draw.region("language"));
        region.setRegionX(region.getRegionX() + index * 7 + 1);
        region.setRegionY(region.getRegionY());
        region.setRegionWidth(5);
        region.setRegionHeight(5);
        return region;
    }

}
