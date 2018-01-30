package io.anuke.sanctre.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
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
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

import static io.anuke.ucore.core.Timers.*;

public class Shade extends Enemy {
    static final float shootduration = 10f;
    static final float phaseduration = 60 * 8;

    float shootTime;
    float eyeHeight = 11f;
    Phase phase = Phase.arms;
    float armAngle = 0f;
    float phaseTime = 0f;

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
            Lines.stroke(2f);
            Lines.poly(x, y, 20, 20f, 0f);

            for(int i : Mathf.signs) {
                Lines.stroke(1f);
                Lines.spikes(x, y, 20f, 8f * i, 10, Timers.time());
                Lines.stroke(2f);
                Lines.spikes(x, y, 20f, 5f * i, 10, Timers.time());
            }

            x += vec.x;
            y += vec.y;
            y += raise;

            float ch = 14f;

            Fill.poly(x, y + ch, 3, 30f, Timers.time());

            Angles.circleVectors(3, 25f, Timers.time(), (cx, cy) -> {
                Fill.poly(x + cx, y + cy + ch, 3, 10f, Mathf.atan2(cx, cy) + 30f+180f);
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
            Shaders.distort.hitcolor.set(SColors.taint);

            Graphics.beginShaders(Shaders.distort);

            Angles.circleVectors(5, 14f, Timers.time(), (cx, cy) -> {
                Fill.poly(x + cx, y + cy + 34f, 3, 6f, Mathf.atan2(cx, cy) + 30f+180f);
            });

            drawCharacter();

            Graphics.endShaders();

            Draw.color(SColors.taintLight);

            Draw.alpha(shootTime);
            Lines.stroke(5f * shootTime);
            Lines.poly(x, y + 34f, 3, 5, Timers.time());

            Draw.color(SColors.taint, Color.WHITE, shootTime);
            Lines.stroke(1f + shootTime * 2f);

            Draw.alpha(1f);
            Lines.poly(x, y + 34f, 3, 5, Timers.time());

            Draw.reset();

            x = px;
            y = py;
        });
    }

    private void drawCharacter(){
        float a = -armAngle();

        Draw.grect("shade", x, y);
        int originx = 25;

        for(boolean b : Mathf.booleans){
            Draw.grect("shade-arm", x, y,
                    originx * -Mathf.sign(b),
                    40 - 10,
                    a * Mathf.sign(b), b);
        }

    }

    @Override
    public void move(){
        if(shootTime > 0f){
            shootTime -= Timers.delta() * 1f / shootduration;
            shootTime = Mathf.clamp(shootTime);
        }

        target = Vars.player;

        phaseTime += Timers.delta();
        if(phaseTime > phaseduration){
            next();
        }

        phase.update(this);
    }

    private float armAngle(){
        return Mathf.absin(Timers.time(), 16f, 8f) + armAngle;
    }

    @Override
    public void shoot(BulletType type, float angle){
        Vector2 v = displace();
        shoot(type, x + v.x * 2f, y + height + v.y * 2f + eyeHeight, angle);
        shootTime = 1f;
    }

    public void shootArms(BulletType type){
        Vector2 v = displace();
        shootTime = 0.5f;

        for(int i : Mathf.signs){
            Tmp.v1.set(0f, - 23f).rotate(armAngle() * i);
            Effects.effect(EnemyFx.shadeshoot, x + v.x * 2f + Tmp.v1.x + i*6f, y + 30f + v.y * 2f + Tmp.v1.y, armAngle() * i - 90);
            shoot(type, x + v.x * 2f + Tmp.v1.x + i*6f, y + 30f + v.y * 2f + Tmp.v1.y, armAngle() * i - 90);
        }
    }

    public void effect(Effect effect, float angle){
        Vector2 v = displace();
        Effects.effect(effect, x + v.x * 2f, y + height + v.y * 2f + eyeHeight, angle);
    }

    public float angleTo(){
        Vector2 v = displace();
        return angleTo(target, v.x * 2f, height + v.y * 2f + eyeHeight);

    }

    public void next(){
        phase.reset(this);
        phase = Phase.values()[Mathf.mod(phase.ordinal() + 1, Phase.values().length)];
        phaseTime = 0f;
    }

    public boolean armsTo(float angle){
        armAngle = Mathf.lerpDelta(armAngle, angle, 0.3f);
        return Mathf.near(angle, armAngle, 1f);
    }

    private Vector2 displace(){
        return Tmp.v3.set(x, y).sub(Core.camera.position.x, Core.camera.position.y).scl(1f/22f).limit(11f);
    }

    public void moveTo(float speed){
        Interpolation i = Interpolation.pow2Out;
        x = i.apply(x, target.x, speed * Timers.delta());
        y = i.apply(y, target.y, speed * Timers.delta());
    }

    enum Phase{
        arms{
            void update(Shade s){
                s.armsTo(Mathf.absin(Timers.time(), 10f, 184f) - 5f);
                if(get(s, "fsh", 6)){
                    s.shootArms(ShadeBullets.ball);
                }
            }
        },
        circle{
            float angle = 0f;
            float speed = 0f;
            float rev = 1f;

            void update(Shade s){
                if(get(s, "dash", 15 - speed)){
                    Angles.circle(3, f -> {
                        s.shoot(ShadeBullets.laser, angle + f);
                    });
                    angle += 15 - speed/2f;
                    speed += 0.7f - speed / 15f;
                }

                if(speed > 9f && rev > 0){
                    rev = -1f;

                    Angles.circle(20, f -> {
                        s.shoot(ShadeBullets.ball, f);
                    });
                }
            }

            void reset(Shade s){
                speed = 0f;
                rev = 1f;
            }
        },
        laser{
            float time = 0f;
            float reload = 50f;
            float angle = 0f;
            boolean shot = false;

            void update(Shade s){
                if(time < reload){
                    if(!shot && time > reload - 20){
                        angle = s.angleTo();
                        shot = true;
                    }
                    time += Timers.delta();

                    if(time > 10f && get(s, "lfx1", 10)){
                        s.effect(EnemyFx.lasercharge, 0);
                    }

                    if(time > reload - 40f && get(s, "lfx2", reload)){
                        s.effect(EnemyFx.laserprecharge, 0);
                    }
                }else{
                    s.effect(EnemyFx.lasersine, angle);
                    s.shoot(ShadeBullets.biglaser, angle);
                    Effects.shake(12f, 13f, s.x, s.y);
                    time = 0f;
                    shot = false;
                }

                s.shootTime = time / reload;
            }
        },
        balls{
            float angle = 0f;

            void update(Shade s){

                if(get(s, "balls", 60)){
                    for(int i =0; i < 8; i ++){
                        run(2.7f * i, () -> {
                            Angles.circle(3, c -> {
                                s.shoot(ShadeBullets.ball, c + angle);
                            });

                            angle += 16f;
                        });
                    }
                }
            }
        };

        abstract void update(Shade s);
        void reset(Shade s){}
    }
}
