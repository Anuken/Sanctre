package io.anuke.sanctre.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import io.anuke.sanctre.Vars;
import io.anuke.sanctre.entities.BulletType;
import io.anuke.sanctre.entities.Enemy;
import io.anuke.sanctre.graphics.Emitter;
import io.anuke.sanctre.graphics.Emitter.Particle;
import io.anuke.sanctre.graphics.Palette;
import io.anuke.sanctre.graphics.effects.EnemyFx;
import io.anuke.ucore.core.*;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.function.Consumer;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

import static io.anuke.sanctre.graphics.Palette.shadeDark;
import static io.anuke.sanctre.graphics.Palette.taintLight;
import static io.anuke.ucore.util.Tmp.*;

import static io.anuke.sanctre.graphics.Palette.taint;
import static io.anuke.sanctre.graphics.Shaders.distort;

public class Shade extends Enemy {
    static final float shootduration = 10f;
    static final float phaseduration = 60 * 2;
    static final float eyeHeight = 11f;

    private ShadePhase phase = ShadePhase.tris;

    Tri[] tris = {new Tri(), new Tri(), new Tri()};
    float shootTime;
    float armAngle = 0f;
    float phaseTime = 0f;
    float trirot;

    public Shade(){
        emitter = new Emitter(30);
        emitter.yMin = 5f;
        emitter.xRange = 10f;
        emitter.particleLife = 120f;
        emitter.relative = true;

        hitbox.set(0, 24f, 32, 42f);
        hitshake = 3f;
        height = 24f;

        phase.begin(this);
    }

    @Override
    public void draw(){

        draw(Sorter.object, Sorter.dark, () -> {
            float px = x, py = y;
            float raise = Mathf.absin(Timers.time(), 23f, 3f);

            Vector2 vec = v3.set(x, y).sub(Core.camera.position.x, Core.camera.position.y).scl(1f/22f).limit(11f);

            Draw.color(shadeDark);
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

            Fill.poly(x, y + ch, 3, 30f, trirot - 30f);

            Lines.stroke(1f);

            for(int i = 0; i < 3; i ++){
                float ang = i*120 + trirot;
                float len = 25f + tris[i].length;
                tr.trns(ang, len);

                Fill.poly(x + tr.x, y + tr.y + ch, 3, 10f, tris[i].angle + 30);

                Draw.color(shadeDark, taintLight, tris[i].charge);
                Lines.poly(x + tr.x, y + tr.y + ch, 3, 18f, tris[i].angle + 30);
                Draw.color(shadeDark);
            }

            x += vec.x;
            y += vec.y;

            Draw.color(Palette.shade);

            for(Particle p : emitter.particles){
                float rad = p.sfract() * 10f;
                Draw.rect("circle", x + p.x, y + p.y, rad, rad);
            }

            distort.offsetx = x;
            distort.offsety = y;
            distort.hittime = hittime;
            distort.hitcolor.set(taint);

            Graphics.beginShaders(distort);

            Angles.circleVectors(5, 14f, Timers.time(), (cx, cy) -> {
                Fill.poly(x + cx, y + cy + 34f, 3, 6f, Mathf.atan2(cx, cy) + 30f);
            });

            drawCharacter();

            Graphics.endShaders();

            Draw.color(Palette.taintLight);

            Draw.alpha(shootTime);
            Lines.stroke(5f * shootTime);
            Lines.poly(x, y + 34f, 3, 5, Timers.time());

            Draw.color(taint, Color.WHITE, shootTime);
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

    private float armAngle(){
        return Mathf.absin(Timers.time(), 16f, 8f) + armAngle;
    }

    private Vector2 displace(){
        return v3.set(x, y).sub(Core.camera.position.x, Core.camera.position.y).scl(1f/22f).limit(11f);
    }

    @Override
    public void move(){
        if(shootTime > 0f){
            shootTime -= Timers.delta() * 1f / shootduration;
            shootTime = Mathf.clamp(shootTime);
        }

        target = Vars.player;

        phaseTime += Timers.delta();

        if(phase.rotate){
            trirot += Timers.delta();
        }

        if(phase.rotateMin){
            for(int i = 0; i < 3; i ++){
                tris[i].angle = i*120 + trirot;
                tris[i].charge = Mathf.lerpDelta(tris[i].charge, 0f, 0.1f);
                tris[i].length = Mathf.lerpDelta(tris[i].length, 0f, 0.1f);
            }
        }

        if(phaseTime > phaseduration){
            next();
        }

        phase.update(this);
    }

    @Override
    public void shoot(BulletType type, float angle){
        Vector2 v = displace();
        shoot(type, x + v.x * 2f, y + height + v.y * 2f + eyeHeight, angle);
        shootTime = 1f;
    }

    void shootArms(BulletType type){
        Vector2 v = displace();
        shootTime = 0.5f;

        for(int i : Mathf.signs){
            v1.set(0f, - 23f).rotate(armAngle() * i);
            Effects.effect(EnemyFx.shadeshoot, x + v.x * 2f + v1.x + i*6f, y + 30f + v.y * 2f + v1.y, armAngle() * i - 90);
            shoot(type, x + v.x * 2f + v1.x + i*6f, y + 30f + v.y * 2f + v1.y, armAngle() * i - 90);
        }
    }

    void effect(Effect effect, float angle){
        Vector2 v = displace();
        Effects.effect(effect, x + v.x * 2f, y + height + v.y * 2f + eyeHeight, angle);
    }

    float angleTo(){
        Vector2 v = displace();
        return angleTo(target, v.x * 2f, height + v.y * 2f + eyeHeight);
    }

    void next(){
        phase.end(this);
        phase = ShadePhase.values()[Mathf.mod(phase.ordinal() + 1, ShadePhase.values().length)];
        phase.begin(this);
        phaseTime = 0f;
    }

    void set(ShadePhase next){
        phase.end(this);
        phase = next;
        next.begin(this);
    }

    boolean armsTo(float angle){
        armAngle = Mathf.lerpDelta(armAngle, angle, 0.3f);
        return Mathf.near(angle, armAngle, 1f);
    }

    void tris(Consumer<Tri> cons){
        for(int i = 0; i < 3; i ++){
            float ang = i*120 + trirot;
            float len = 25f + tris[i].length;
            tr.trns(ang, len);
            tris[i].x = x + displace().x + tr.x;
            tris[i].y = y + displace().y + tr.y + 14f;
            cons.accept(tris[i]);
        }
    }

    void moveTo(float speed){
        Interpolation i = Interpolation.pow2Out;
        x = i.apply(x, target.x, speed * Timers.delta());
        y = i.apply(y, target.y, speed * Timers.delta());
    }

    class Tri{
        float angle, length, charge, x, y;
    }
}
