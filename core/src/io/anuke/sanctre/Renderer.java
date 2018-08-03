package io.anuke.sanctre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pools;
import io.anuke.sanctre.entities.FacetEffect;
import io.anuke.sanctre.graphics.BlockRenderer;
import io.anuke.sanctre.graphics.DecalRenderer;
import io.anuke.sanctre.graphics.LightRenderer;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.entities.impl.SolidEntity;
import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.facet.FacetLayer;
import io.anuke.ucore.facet.FacetLayerHandler;
import io.anuke.ucore.facet.Facets;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.graphics.Surface;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.util.Atlas;
import io.anuke.ucore.util.Tmp;

import static io.anuke.ucore.core.Core.*;

public class Renderer extends RendererModule {
    BlockRenderer blocks;
    LightRenderer lights;
    DecalRenderer decals;

    public Surface darkSurface;
    public Surface blurSurface;

    public Renderer(){
        atlas = new Atlas("sprites.atlas");
        blocks = new BlockRenderer();
        lights = new LightRenderer();
        decals = new DecalRenderer();

        cameraScale = 3;
        pixelate();

        blurSurface = Graphics.createSurface();

        FacetLayerHandler hand = new FacetLayerHandler();

        FacetLayer darkLayer = new FacetLayer("darkness", Sorter.dark, 0){
            @Override
            public void begin(){
                Graphics.surface(surface);
                Draw.color(0f, 0f, 0f, 0.99f);
                Draw.rect(blurSurface.texture(), camera.position.x, camera.position.y, camera.viewportWidth, -camera.viewportHeight);
                Draw.reset();
            }
        };

        hand.allDrawLayers.set(hand.allDrawLayers.size -1, darkLayer);
        darkSurface = darkLayer.surface;

        Facets.instance().setLayerManager(hand);

        Effects.setEffectProvider((effect, color, x, y, rotation, data) -> {
            FacetEffect f = Pools.obtain(FacetEffect.class);
            f.color = color;
            f.effect = effect;
            f.rotation = rotation;
            f.data = data;
            f.set(x, y);
            f.add();
        });
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

        Graphics.surface(blurSurface);
        Draw.rect(darkSurface.texture(), camera.position.x, camera.position.y, camera.viewportWidth, -camera.viewportHeight);
        Graphics.surface();

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
            s.getHitbox(Tmp.r1);
            Draw.color(Color.GREEN);
            Lines.rect(Tmp.r1);

            s.getHitboxTile(Tmp.r1);
            Draw.color(Color.ORANGE);
            Lines.rect(Tmp.r1);
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
