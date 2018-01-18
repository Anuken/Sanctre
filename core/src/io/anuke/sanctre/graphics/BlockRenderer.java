package io.anuke.sanctre.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pools;
import static io.anuke.sanctre.Vars.*;

import io.anuke.sanctre.Vars;
import io.anuke.sanctre.world.Tile;
import io.anuke.sanctre.world.blocks.Blocks;
import io.anuke.ucore.UCore;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.facet.FacetList;
import io.anuke.ucore.graphics.CacheBatch;
import io.anuke.ucore.util.Mathf;

public class BlockRenderer {
    private final int chunksize = 32;
    private CacheBatch batch = new CacheBatch(1024*1024);
    private int[][] cache;

    private FacetList[][] facets, writeback;
    private boolean resized = true;
    private int lastcamx, lastcamy;


    public void drawFloor(){
        Graphics.end();

        if(cache == null || cache.length != world.width() / chunksize + 1
                || cache[0].length != world.height() / chunksize + 1){
            cache = new int[world.width() / chunksize + 1][world.height() / chunksize + 1];
            recache();
        }

        OrthographicCamera camera = Core.camera;

        int crangex = Math.round(camera.viewportWidth * camera.zoom / (chunksize * tilesize)) + 1;
        int crangey = Math.round(camera.viewportHeight * camera.zoom / (chunksize * tilesize)) + 1;

        batch.setProjectionMatrix(Core.camera.combined);
        batch.beginDraw();

        //render tile chunks
        for(int x = -crangex; x <= crangex; x++){
            for(int y = crangey; y >= -crangey; y--){
                int worldx = Mathf.scl(camera.position.x, chunksize * tilesize) + x;
                int worldy = Mathf.scl(camera.position.y, chunksize * tilesize) + y;

                if(!Mathf.inBounds(worldx, worldy, cache)){
                    continue;
                }
                Gdx.gl.glEnable(GL20.GL_BLEND);
                batch.drawCache(cache[worldx][worldy]);
            }
        }

        batch.endDraw();

        Graphics.begin();
    }

    public void drawBlocks(){
        OrthographicCamera camera = Core.camera;

        int camx = Mathf.scl(camera.position.x, tilesize);
        int camy = Mathf.scl(camera.position.y, tilesize);

        int padding = 6;
        //view range x/y (block only)
        int vrx = Mathf.scl2(camera.viewportWidth * camera.zoom, tilesize) + padding;
        int vry = Mathf.scl2(camera.viewportHeight * camera.zoom, tilesize) + padding;


        if(facets == null){
            facets = new FacetList[vrx][vry];
            writeback = new FacetList[vrx][vry];
            resized = true;
        }

        //change renderable list size on screen resize/startup
        if(facets.length != vrx || facets[0].length != vry){

            for(int rx = 0; rx < facets.length; rx++){
                System.arraycopy(facets[rx], 0, writeback[rx], 0, facets[0].length);
            }

            facets = new FacetList[vrx][vry];

            for(int rx = 0; rx < writeback.length; rx++){
                for(int ry = 0; ry < writeback[0].length; ry++){
                    free(writeback[rx][ry]);
                }
            }

            //invalidate cam position
            lastcamx = -999;
            lastcamy = -999;

            writeback = new FacetList[vrx][vry];

            resized = true;
        }

        //if the camera moved, re-render everything
        if(lastcamx != camx || lastcamy != camy){

            if(!resized){
                int shiftx = -(camx - lastcamx);
                int shifty = -(camy - lastcamy);

                //clear writeback
                for(int x = 0; x < vrx; x++){
                    for(int y = 0; y < vry; y++){
                        writeback[x][y] = null;
                    }
                }

                //shift everything
                for(int x = 0; x < vrx; x++){
                    for(int y = 0; y < vry; y++){

                        int targetx = x + shiftx;
                        int targety = y + shifty;

                        if(!Mathf.inBounds(targetx, targety, facets)){
                            free(facets[x][y]);
                        }else{
                            writeback[targetx][targety] = facets[x][y];
                            facets[x][y] = null;
                        }
                    }
                }

                for(int x = 0; x < vrx; x++){
                    System.arraycopy(writeback[x], 0, facets[x], 0, vry);
                }

            }

            for(int x = 0; x < vrx; x++){
                for(int y = 0; y < vry; y++){
                    int worldx = x - vrx / 2 + camx;
                    int worldy = y - vry / 2 + camy;

                    if(facets[x][y] != null){
                        continue;
                    }else{
                        facets[x][y] = Pools.obtain(FacetList.class);
                    }

                    Tile tile = Vars.world.get(worldx, worldy);

                    if(tile == null)
                        continue;

                    if(tile.wall() != Blocks.air){
                        tile.wall().draw(facets[x][y], tile);
                    }
                }
            }

            lastcamx = camx;
            lastcamy = camy;

            resized = false;
        }
    }

    private void free(FacetList list){
        if(list != null){
            list.free();
        }
    }

    public void resetWalls(){
        lastcamx = -99999;
        lastcamy = -99999;
    }

    public void recache(){
        batch.clear();
        Graphics.useBatch(batch);
        int crx = world.width() / chunksize + 1;
        int cry = world.height() / chunksize + 1;

        for(int x = 0; x < crx; x ++){
            for(int y = 0; y < cry; y ++){
                batch.begin();
                for(int cx = 0; cx < chunksize; cx ++){
                    for(int cy = 0; cy < chunksize; cy ++){
                        Tile tile = world.get(x * chunksize + cx, y * chunksize + cy);
                        if(tile != null){
                            tile.floor().draw(null, tile);
                        }
                    }
                }
                for(int cx = 0; cx < chunksize; cx ++){
                    for(int cy = 0; cy < chunksize; cy ++){
                        Tile tile = world.get(x * chunksize + cx, y * chunksize + cy);
                        if(tile != null){
                            tile.decal().draw(null, tile);
                        }
                    }
                }
                batch.end();
                cache[x][y] = batch.getLastCache();
            }
        }

        Graphics.popBatch();
    }
}
