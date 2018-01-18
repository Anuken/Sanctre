package io.anuke.sanctre.entities;

import com.badlogic.gdx.math.Vector2;
import io.anuke.sanctre.items.Item;
import io.anuke.sanctre.items.Items;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.util.Angles;

import static io.anuke.sanctre.entities.Direction.*;

public class Player extends Spark {
    static float speed = 0.7f;

    Direction direction = front;
    float walktime = 0f;
    Item weapon = Items.marbleSword;

    public Player(){
        hitbox.width = 4f;
        hitbox.height = 8f;
        hitbox.offsety -= 1f;
    }

    @Override
    public boolean collides(SolidEntity other) {
        return other instanceof Bullet && !(((Bullet) other).owner instanceof Player);
    }

    @Override
    public void draw(){
        draw(b -> {
            float cx = x, cy = y;
            x = (int)x;
            y = (int)y;

            b.layer = y;

            float angle = Angles.mouseAngle(x, y + 5f);

            boolean drawBefore = angle > 0 && angle < 180 && weapon != null;

            if(drawBefore){
                weapon.draw(this);
            }

            Draw.grect("player-" + direction.texture +
                    (walktime > 0 ? "-walk" + (1 + (int)(walktime / 10f) % 2) : ""), x, y, direction.flipped);

            if(weapon == null){
                int ox = direction == Direction.left ? -1 : 0;
                Draw.rect("hand", x - 2 + ox, y + 2);
                Draw.rect("hand", x + 2 + ox, y + 2);
            }else{
                getDirection(Angles.mouseAngle(x, y + 5f));
            }

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
