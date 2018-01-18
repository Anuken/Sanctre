package io.anuke.sanctre.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import io.anuke.sanctre.Vars;
import io.anuke.sanctre.entities.Bullets;
import io.anuke.sanctre.entities.Enemy;
import io.anuke.sanctre.graphics.Emitter;
import io.anuke.sanctre.graphics.Emitter.Particle;
import io.anuke.sanctre.graphics.SColors;
import io.anuke.sanctre.graphics.Shaders;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

public class Shade extends Enemy {

    public Shade(){
        emitter = new Emitter(30);
        emitter.yMin = 5f;
        emitter.xRange = 10f;
        emitter.particleLife = 120f;

        hitbox.bounds(0, 24f, 32, 42f);
    }

    @Override
    public void draw(){

        draw(Sorter.object, Sorter.dark, () -> {
            float px = x, py = y;
            float raise = Mathf.absin(Timers.time(), 23f, 3f);

            Vector2 vec = Tmp.v3.set(x, y).sub(Core.camera.position.x, Core.camera.position.y).scl(1f/22f).limit(11f);

            Draw.colorl(0.1f);
            Draw.thick(2f);
            Draw.polygon(x, y, 20, 20f, 0f);

            for(int i : Mathf.signs) {
                Draw.thick(1f);
                Draw.spikes(x, y, 20f, 8f * i, 10, Timers.time());
                Draw.thick(2f);
                Draw.spikes(x, y, 20f, 5f * i, 10, Timers.time());
            }

            x += vec.x;
            y += vec.y;
            y += raise;

            float ch = 14f;

            Draw.shape(x, y + ch, 3, 30f, Timers.time());

            Angles.circleVectors(3, 25f, 30f + Timers.time(), (cx, cy) -> {
                Draw.shape(x + cx, y + cy + ch, 3, 10f, Mathf.atan2(cx, cy) + 30f+180f);
            });

            x += vec.x;
            y += vec.y;

            Draw.color(SColors.shade);

            for(Particle p : emitter.particles){
                float rad = p.sfract() * 10f;
                Draw.rect("circle", p.x, p.y, rad, rad);
            }

            Graphics.beginShaders(Shaders.distort);

            Draw.grect("shade", x, y);

            Draw.thick(1f);
            Draw.lineAngle(x, y, 90, 15f);

            Graphics.endShaders();

            Draw.color(SColors.taint);
            Draw.thick(1f);

            Draw.polygon(x, y + 34f, 3, 5, Timers.time());

            Draw.reset();

            x = px;
            y = py;
        });
    }

    @Override
    public void move(){
        target = Vars.player;

        if(Timers.get(this, "reload", 50)){
            shoot(Bullets.orb, angleTo(target));
        }

    }


}
