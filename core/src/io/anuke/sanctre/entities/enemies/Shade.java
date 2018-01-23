package io.anuke.sanctre.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.anuke.sanctre.Vars;
import io.anuke.sanctre.entities.BulletType;
import io.anuke.sanctre.entities.Enemy;
import io.anuke.sanctre.entities.bullets.ShadeBullets;
import io.anuke.sanctre.graphics.Emitter;
import io.anuke.sanctre.graphics.Emitter.Particle;
import io.anuke.sanctre.graphics.SColors;
import io.anuke.sanctre.graphics.Shaders;
import io.anuke.sanctre.graphics.effects.EnemyFx;
import io.anuke.ucore.core.*;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

public class Shade extends Enemy {
    static final float shootduration = 10f;
    float shootTime;
    float eyeHeight = 11f;
    Phase phase = Phase.laser;

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

            Draw.color(SColors.taintLight);

            Draw.alpha(shootTime);
            Draw.thick(5f * shootTime);
            Draw.polygon(x, y + 34f, 3, 5, Timers.time());

            Draw.color(SColors.taint, Color.WHITE, shootTime);
            Draw.thick(1f + shootTime * 2f);

            Draw.alpha(1f);
            Draw.polygon(x, y + 34f, 3, 5, Timers.time());

            Draw.reset();

            x = px;
            y = py;
        });
    }

    @Override
    public void shoot(BulletType type, float angle){
        Vector2 v = displace();
        shoot(type, x + v.x * 2f, y + height + v.y * 2f + eyeHeight, angle);
        shootTime = 1f;
    }

    public void effect(Effect effect, float angle){
        Vector2 v = displace();
        Effects.effect(effect, x + v.x * 2f, y + height + v.y * 2f + eyeHeight, angle);
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

        phase.update(this);
        //if(Mathf.chance(0.01 * Timers.delta())){
        //    phase = Phase.values()[Mathf.mod(phase.ordinal() + 1, Phase.values().length)];
        //}
    }

    private Vector2 displace(){
        Vector2 vec = Tmp.v3.set(x, y).sub(Core.camera.position.x, Core.camera.position.y).scl(1f/22f).limit(11f);
        return vec;
    }

    enum Phase{
        circle{
            float angle = 0f;

            public void update(Shade s){

                if(Timers.get(s, "reload", 2)){
                    Angles.circle(4, c -> {
                        Angles.shotgun(3, 20f, angle + c, f -> {
                            s.shoot(ShadeBullets.orb, f);
                        });
                    });

                    angle += 5f;
                }

                s.move(Mathf.cos(s.y, 10f, 5f), Mathf.sin(s.x, 10f, 5f));
            }
        },
        laser{
            float time = 0f;
            float reload = 170f;

            public void update(Shade s){
                if(time < reload){
                    time += Timers.delta();
                    if(time > 40f && Timers.get(s, "lfx1", 10) && time < reload - 30f){
                        s.effect(EnemyFx.lasercharge, 0);
                    }

                    if(time > reload - 40f && Timers.get(s, "lfx2", reload)){
                        s.effect(EnemyFx.laserprecharge, 0);
                    }
                }else{
                    s.effect(EnemyFx.lasersine, s.angleTo());
                    s.shoot(ShadeBullets.biglaser, s.angleTo());
                    Effects.shake(12f, 13f, s.x, s.y);
                    time = 0f;
                }

                s.shootTime = time / reload;
            }
        },
        balls{
            float angle = 0f;

            public void update(Shade s){

                if(Timers.get(s, "balls", 10)){
                    for(int i = 0; i < 5; i ++){
                        Timers.run(20f + i * 7f, () -> {
                            Angles.circle(4, c -> {
                                s.shoot(ShadeBullets.ball, c + angle);
                            });

                            angle += 5f;
                        });
                    }
                }
            }
        };

        public abstract void update(Shade s);
    }
}
