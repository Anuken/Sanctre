package io.anuke.sanctre.entities.enemies;

import com.badlogic.gdx.graphics.Color;
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
    static final float shootduration = 10f;
    float shootTime;
    float eyeHeight = 11f;

    public Shade(){
        emitter = new Emitter(30);
        emitter.yMin = 5f;
        emitter.xRange = 10f;
        emitter.particleLife = 120f;
        emitter.relative = true;

        hitbox.bounds(0, 24f, 32, 42f);
        hitshake = 3f;
        height = 24f;
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
                Draw.rect("circle", x + p.x, y + p.y, rad, rad);
            }

            Shaders.distort.offsetx = x;
            Shaders.distort.offsety = y;
            Shaders.distort.hittime = hittime;

            Graphics.beginShaders(Shaders.distort);

            Draw.grect("shade", x, y);

            Draw.thick(1f);
            Draw.lineAngle(x, y, 90, 15f);

            Graphics.endShaders();

            Draw.color(Color.ORANGE);

            Draw.alpha(shootTime);
            Draw.thick(5f * shootTime);
            Draw.polygon(x, y + 34f, 3, 5 + shootTime*2f, Timers.time());

            Draw.color(SColors.taint, Color.WHITE, shootTime);
            Draw.thick(1f + shootTime * 2f);

            Draw.alpha(1f);
            Draw.polygon(x, y + 34f, 3, 5 + shootTime*2f, Timers.time());

            Draw.reset();

            x = px;
            y = py;
        });
    }

    @Override
    public void shoot(Bullets type, float angle){
        Vector2 v = displace();
        shoot(type, x + v.x * 2f, y + height + v.y * 2f + eyeHeight, angle);
        shootTime = 1f;
    }

    public float angleTo(){
        Vector2 v = displace();
        return angleTo(target, v.x * 2f, height + v.y * 2f + eyeHeight);

    }

    @Override
    public void move(){
        if(shootTime > 0f){
            shootTime -= Timers.delta() * 1f / shootduration;
            shootTime = Mathf.clamp(shootTime);
        }

        target = Vars.player;

        if(Timers.get(this, "reload", 50)){
            Angles.shotgun(1, 5f, angleTo(), f -> {
                shoot(Bullets.laser, f);
            });
        }

        //move(Mathf.cos(y, 10f, 2f), Mathf.sin(x, 10f, 2f));

    }

    private Vector2 displace(){
        Vector2 vec = Tmp.v3.set(x, y).sub(Core.camera.position.x, Core.camera.position.y).scl(1f/22f).limit(11f);
        return vec;
    }


}
