package io.anuke.sanctre.entities;

import com.badlogic.gdx.math.Rectangle;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.entities.BulletEntity;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.facet.BaseFacet;
import io.anuke.ucore.facet.FacetList;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Physics;
import io.anuke.ucore.util.Tmp;

public class Bullet extends BulletEntity {
    public FacetList facets = new FacetList();

    public Bullet(BulletType type, Entity owner, float x, float y, float angle){
        super(type, owner, angle);
        set(x, y);

        if(type.line){
            hitbox.setSize(type.length*2f);
        }
    }

    @Override
    public boolean collides(SolidEntity other){
        BulletType type = (BulletType)this.type;

        if(other instanceof Bullet){
            Bullet bullet = (Bullet)other;
            return (bullet.type().block || type.block) && !(type().line || bullet.type().line) && bullet.owner != owner;
        }else{
            if(type.line){
                Rectangle hit = other.hitbox.getRect(other.x, other.y);
                Tmp.t1.trns(angle(), type.length);
                return Physics.raycastRect(x, y, x + Tmp.t1.x, y + Tmp.t1.y, hit) != null;
            }else {
                return other != owner;
            }
        }
    }

    @Override
    public void collision(SolidEntity other){
        if(other instanceof Bullet){
            if(type().block){
                Effects.effect(type().hitEffect, this);
            }
            float angle = angleTo(other);
            velocity.setAngle(angle + 180f);
        }else if(!type().line){
            remove();
            type.removed(this);
        }
    }

    @Override
    public void added(){
        super.added();

        BulletType type = (BulletType)this.type;

        facets.add(new BaseFacet(type.dark ? Sorter.dark : y - 10f, Sorter.object, p->{
            if(!type.dark) p.layer = y - 10f;
            type.draw(this);
        }));
    }

    @Override
    public void removed(){
        if(type().line && type().lineEffect != null){
            float ang = angle();

            Tmp.t1.trns(ang, 1f);
            Tmp.v1.set(Tmp.t1);

            for(int i = 0; i < type().effects; i ++){
                float fract = i / (float)type().effects;

                Tmp.v2.set(Tmp.v1).scl(type().length * fract);

                Effects.effect(type().lineEffect, x + Tmp.v2.x + Mathf.range(3f),
                        y + Tmp.v2.y + Mathf.range(3f), ang + Mathf.range(15f));
            }
        }
        facets.free();
    }

    public BulletType type(){
        return (BulletType)type;
    }

}
