package io.anuke.sanctre.items.types;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.anuke.sanctre.entities.Bullet;
import io.anuke.sanctre.entities.BulletType;
import io.anuke.sanctre.entities.Player;
import io.anuke.sanctre.entities.bullets.WeaponBullets;
import io.anuke.sanctre.graphics.effects.Fx;
import io.anuke.sanctre.items.Weapon;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.Hitbox;
import io.anuke.ucore.graphics.CapStyle;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

public class Sword extends Weapon {
    public float arc = 150f;
    public float swingduration = 7f;
    public float height = 4f;
    public float length = 14f;
    public float basethick = 7f;
    public float swingspeed = 30f;
    public float dashthick = 15f;
    public float dashspeed = 34f;
    public float lungeSpeed = 14f;
    public BulletType bullet = WeaponBullets.slash;
    public Color startColor = Color.WHITE, endColor = Color.LIGHT_GRAY;

    float swingtime = 0f;
    float swingreload = 0f;
    float dashreload = 0f;
    boolean swinging = false;
    boolean direction = true;
    Array<Vector3> removals = new Array<>();
    Array<Vector3> points = new Array<>();
    Array<Vector3> dashes = new Array<>();

    public Sword(String name) {
        super(name);
    }

    @Override
    public void update(Player player){
        if(Inputs.keyTap("attack1") && !swinging && swingreload <= 0f){
            swinging = true;
            direction = swingtime < 0.5f;
            Effects.shake(3f, 3f, player);
            dashreload = 0.1f;
            swingreload = 1f;
        }

        if(Inputs.keyTap("dash") && !swinging && dashreload <= 0f){
            Tmp.t1.trns(Angles.mouseAngle(player.x, player.y + height), lungeSpeed);
            player.impulseMove(Tmp.t1);
            swingreload = 0f;
            dashreload = 1f;
            Effects.shake(2f, 3f, player);
        }

        if(swingreload > 0f){
            swingreload -= Timers.delta() * 1f / swingspeed;
        }

        if(dashreload > 0f){
            dashreload -= Timers.delta() * 1f / dashspeed;

            if(dashreload > 1f - (1f / dashspeed * 7f)) dashes.add(new Vector3(player.x, player.y + height, 0f));
        }

        if(swinging){

            float move = 1f / swingduration * Timers.delta();
            float baseang = Angles.mouseAngle(player.x, player.y + height);

            for(float f = 0; f <= move; f += 0.02f){
                swingtime += Mathf.sign(direction) * f;
                float angle = Angles.mouseAngle(player.x, player.y + height) + getAngleOffset();
                Tmp.t1.trns(angle, length);
                points.add(new Vector3(Tmp.t1.x, Tmp.t1.y, 0f));
                swingtime -= Mathf.sign(direction) * f;
                if(f <= 0f){
                    player.shoot(bullet, player.x + Tmp.t1.x, player.y + height + Tmp.t1.y, angle);
                }
            }

            //deflect bullets
            Entities.getNearby(player.x, player.y + height, length*4f, e -> {
                if(!(e instanceof Bullet)) return;
                Bullet b = (Bullet)e;

                if(b.owner instanceof Player) return;

                Hitbox box = e.hitbox;
                float angle = Angles.angle(player.x, player.y + height, e.x + box.offsetx, e.y + box.offsety);

                float first = baseang + Mathf.sign(direction) * arc/2f;
                float current = baseang + getAngleOffset();
                boolean valid;
                if(!direction){
                    valid = angle > first && angle < current || angle > first % 360f && angle < current % 360f;
                }else{
                    valid = angle < first && angle > current || angle < first % 360f && angle > current % 360f;
                }

                if(b.distanceTo(player.x, player.y + height) <= length*2f && valid){
                    b.velocity.setAngle(b.angleTo(player.x, player.y + height) + 180f);
                    b.velocity.scl(1.1f);
                    b.velocity.add(player.velocity.cpy().scl(0.5f));
                    Effects.effect(Fx.swordspark, b.x, b.y, b.angle());
                    swingreload = -1f;
                }
            });

            swingtime += move * Mathf.sign(direction);
            if(swingtime >= 1f && direction){
                swingtime = 1f;
                swinging = false;
            }else if(swingtime <= 0f && !direction){
                swingtime = 0f;
                swinging = false;
            }
        }
    }

    @Override
    public void draw(Player player){
        float angle = Angles.mouseAngle(player.x, player.y + height) + getAngleOffset();

        Tmp.t1.trns(angle, 1.5f);

        Draw.grect(name, player.x, player.y + height, angle - 90);

        Tmp.t1.setLength(2f);
        Draw.rect("hand", player.x + Tmp.t1.x, player.y + height + Tmp.t1.y);

        Tmp.t1.setLength(1f);
        Draw.rect("hand", player.x + Tmp.t1.y, player.y + height + Tmp.t1.y);
    }

    @Override
    public void drawOver(Player player){
        removals.clear();

        for(int i = 0; i < points.size; i ++){
            Vector3 cur = points.get(i);
            float offsetx = player.x, offsety = player.y;
            float thick = (1f - cur.z) * basethick * Mathf.lerp((float)i / (points.size-1), 1f, 0.4f);
            Draw.color(startColor, endColor, cur.z);

            if(i != points.size-1) {
                Vector3 next = points.get(i + 1);

                Lines.stroke(thick);
                Lines.line(offsetx + cur.x, offsety + cur.y, offsetx + next.x, offsety + next.y, CapStyle.none, 1f);
            }

            cur.z += 1f/swingduration;
            if(cur.z > 1f){
                removals.add(cur);
            }
        }

        points.removeAll(removals, true);
        removals.clear();

        for(int i = 0; i < dashes.size; i ++){
            Vector3 cur = dashes.get(i);
            float offsetx = 0, offsety = 0;
            float thick = (1f - cur.z) * dashthick * Mathf.lerp((float)i / (dashes.size-1), 1f, 0.1f);

            if(i != dashes.size-1) {
                Vector3 next = dashes.get(i + 1);

                Lines.stroke(thick);
                Lines.line(offsetx + cur.x, offsety + cur.y, offsetx + next.x, offsety + next.y, CapStyle.none, 1f);
            }else{
                float rad = thick*0.75f;
                Draw.rect("circle", cur.x, cur.y, rad, rad);
            }

            cur.z += 1f/9;
            if(cur.z > 1f){
                removals.add(cur);
            }
        }

        dashes.removeAll(removals, true);

        Draw.reset();
    }

    float getAngleOffset(){
        Interpolation i = direction ? Interpolation.pow3Out : Interpolation.pow2In;
        return - arc/2 + i.apply(swingtime) * arc;
    }
}
