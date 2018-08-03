package io.anuke.sanctre.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.graphics.CacheBatch;

import java.util.concurrent.Callable;

public class DecalRenderer {
    private static final int maxDecals = 300;
    private Array<Runnable> draws = new Array<>();
    private CacheBatch batch = new CacheBatch(1024*32);
    private boolean update = false;

    public DecalRenderer(){
        batch.begin();
        batch.end();
    }

    public void addDraw(Runnable call){
        draws.add(call);
        //remove extra junk
        if(draws.size > maxDecals){
            draws.removeRange(0, draws.size - maxDecals);
        }
        update = true;
    }

    public void draw(){
        Graphics.end();

        if(update) {
            Graphics.useBatch(batch);
            batch.begin(0);

            for (Runnable call : draws) {
                call.run();
            }

            batch.end();
            Graphics.popBatch();
            update = false;
        }

        batch.setProjectionMatrix(Core.camera.combined);
        batch.beginDraw();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        batch.drawCache(0);
        batch.endDraw();

        Graphics.begin();
    }
}
