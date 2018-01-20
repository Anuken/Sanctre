package io.anuke.sanctre.entities;

import com.badlogic.gdx.math.Vector2;
import io.anuke.sanctre.graphics.Fx;
import io.anuke.sanctre.graphics.SColors;
import io.anuke.sanctre.graphics.Shaders;
import io.anuke.sanctre.items.Item;
import io.anuke.sanctre.items.Items;
import io.anuke.ucore.core.*;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

import static io.anuke.sanctre.entities.Direction.*;

public class Player extends Spark {
    static float speed = 0.7f;
    static float hitdur = 8f;

    Direction direction = front;
    float walktime = 0f;
    Item weapon = Items.marbleSword;
    float hittime;

    public Player(){
        hitbox.width = 4f;
        hitbox.height = 8f;
        hitbox.offsety -= 1f;
    }

    @Override
    public boolean collides(SolidEntity other) {
        return hittime <= 0f && other instanceof Bullet && !(((Bullet) other).owner instanceof Player);
    }

    @Override
    public void collision(SolidEntity other) {
        super.collision(other);
        if(other instanceof  Bullet) {
            Bullet b = (Bullet)other;
            float angle = other.angleTo(this);
            if(b.getDamage() > 1) Effects.effect(Fx.sparkspatter, x, y, angle);
            Effects.effect(Fx.sparkparticle, x, y, angle);
            Effects.shake(4f, 3f, this);
            Angles.translation(angle, 2f * b.getDamage());
            impulse(Angles.vector);
            hittime = 1f;
        }
    }

    @Override
    public void draw(){
        draw(b -> {
            Shaders.player.hittime = Mathf.clamp(hittime);
            Shaders.player.color.set(SColors.blood);

            float cx = x, cy = y;
            x = (int)x;
            y = (int)y;

            b.layer = y;

            float angle = Angles.mouseAngle(x, y + 5f);

            boolean drawBefore = angle > 0 && angle < 180 && weapon != null;

            if(drawBefore){
                weapon.draw(this);
            }

            Graphics.beginShaders(Shaders.player);

            Draw.grect("player-" + direction.texture +
                    (walktime > 0 ? "-walk" + (1 + (int)(walktime / 10f) % 2) : ""), x, y, direction.flipped);

            if(weapon == null){
                int ox = direction == Direction.left ? -1 : 0;
                Draw.rect("hand", x - 2 + ox, y + 2);
                Draw.rect("hand", x + 2 + ox, y + 2);
            }else{
                getDirection(Angles.mouseAngle(x, y + 5f));
            }

            Graphics.endShaders();

            if(!drawBefore){
                weapon.draw(this);
            }
            weapon.drawOver(this);

            x = cx;
            y = cy;
        });

        drawShadow(8, 0, true);
    }

    @Override
    public void update(){
        float speed = Player.speed * Timers.delta();

        vector.set(0, 0);

        if(Inputs.keyDown("up")){
            vector.add(0, speed);
        }

        if(Inputs.keyDown("down")){
            vector.add(0, -speed);
        }

        if(Inputs.keyDown("left")){
            vector.add(-speed, 0);
        }

        if(Inputs.keyDown("right")){
            vector.add(speed, 0);
        }

        float len = vector.len();

        if(weapon != null) weapon.update(this);

        if(vector.len() > 0.1f && weapon == null) getDirection(vector);

        vector.limit(speed);

        impulse(vector.x, vector.y);
        Vector2 delta = updateVelocity();

        if(delta.len() > 0.01f && len > 0.01f){
            walktime += Timers.delta();
        }else{
            walktime = 0f;
        }

        if(hittime > 0){
            hittime -= 1f/hitdur;
        }
    }

    public void impulseMove(Vector2 vector){
        getDirection(vector);
        impulse(vector);
    }

    public void getDirection(float angle){
        direction = (angle < 45 || angle > 315 ? right : angle >= 45 && angle < 135 ? back : angle >= 135 && angle < 225 ? left : front);;
    }

    public void getDirection(Vector2 vector){
        float angle = vector.angle();
        getDirection(angle);
    }

}
