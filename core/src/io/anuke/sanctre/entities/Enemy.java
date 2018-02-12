package io.anuke.sanctre.entities;

import io.anuke.sanctre.graphics.effects.EnemyFx;
import io.anuke.sanctre.graphics.Emitter;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.util.Angles;

public class Enemy extends Spark {
    public static final float hitduration = 7f;
    public Effect hiteffect = EnemyFx.darkparticle;
    public float hitshake = 0f;

    public float hittime = 0f;
    public Emitter emitter;
    public Player target;

    @Override
    public boolean collides(SolidEntity other) {
        return other instanceof Bullet && ((Bullet)other).owner instanceof Player;
    }

    @Override
    public void collision(SolidEntity other) {
        super.collision(other);
        Bullet b = (Bullet)other;
        Effects.effect(hiteffect, b.x, b.y, b.angle());
        hittime = 1.0f;
        Effects.shake(hitshake, hitshake, this);

        tr.trns(b.angleTo(x, y + height), 0.5f);
        impulse(tr.x, tr.y);
    }

    @Override
    public void added(){
        super.added();
        if(emitter != null)
            emitter.init(this);
    }

    @Override
    public void update(){
        if(emitter != null)
            emitter.update(this);

        updateVelocity();

        if(hittime > 0){
            hittime -=  1f / hitduration * Timers.delta();
        }

        move();
    }

    public void move(){}
}
