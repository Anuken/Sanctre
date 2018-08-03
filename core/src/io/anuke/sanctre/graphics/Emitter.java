package io.anuke.sanctre.graphics;

import com.badlogic.gdx.math.Vector2;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.util.Mathf;

public class Emitter {
    public Particle[] particles;
    public float particleLife = 80;
    public float xRange = 6f, yMin = 0f, yMax = 20f;
    public float speed = 0.4f;
    public boolean emit = true;
    public boolean relative = false;

    public Emitter(int amount){
        this(amount, 6f, 6f);
    }

    public Emitter(int amount, float xrange, float yrange){
        this.xRange = xrange;
        this.yMin = -yrange;
        this.yMax = yrange;

        particles = new Particle[amount];
    }

    public Emitter setEmission(float speed, float life){
        this.particleLife = life;
        this.speed = speed;
        return this;
    }

    public Emitter setParticles(int amount){
        particles = new Particle[amount];
        for(int i = 0; i < particles.length; i ++){
            particles[i] = new Particle();
        }
        return this;
    }

    public void init(Entity entity){
        for(int i = 0; i < particles.length; i ++){
            particles[i] = new Particle();
            particles[i].reset(entity);
            particles[i].life = Mathf.random(particleLife);
        }

        //simulate a bit
        for(int i = 0; i < 60; i ++){
            update(entity, 1f);
        }
    }

    public void update(Entity entity){
        update(entity, Timers.delta());
    }

    public void update(Entity entity, float delta){
        for(Particle part : particles){
            part.life += delta;
            part.y += delta*speed;

            if(part.fract() <= 0f && emit){
                part.reset(entity);
            }
        }

        if(Timers.delta() >= particleLife/5f){
            reset(entity);
        }
    }

    public void reset(Entity entity){
        for(int i = 0; i < particles.length; i ++){
            particles[i].reset(entity);
            particles[i].life = Mathf.random(particleLife);
        }
    }

    public class Particle extends Vector2 {
        float life;

        public float fract(){
            return 1f-life/particleLife;
        }

        public float fin(){
            return life/particleLife;
        }

        public float sfract(){
            return (0.5f-Math.abs(life/particleLife-0.5f))*2f;
        }

        void reset(Entity entity){
            if(relative){
                set(Mathf.range(xRange), Mathf.random(yMin, yMax));
            }else{
                set(Mathf.range(xRange) + entity.getX(), Mathf.random(yMin, yMax) + entity.getY());
            }
            life = 0f;
        }
    }
}
