package io.anuke.sanctre.entities;

import io.anuke.ucore.core.Effects;
import io.anuke.ucore.entities.BulletEntity;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.facet.BaseFacet;
import io.anuke.ucore.facet.FacetList;
import io.anuke.ucore.facet.Sorter;

public class Bullet extends BulletEntity {
    public FacetList facets = new FacetList();

    public Bullet(Bullets type, Entity owner, float x, float y, float angle){
        super(type, owner, angle);
        set(x, y);
    }

    @Override
    public boolean collides(SolidEntity other){
        Bullets type = (Bullets)this.type;

        if(other instanceof Bullet){
            Bullet bullet = (Bullet)other;
            return (bullet.type().block || type.block) && bullet.owner != owner;
        }else{
            return other != owner;
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
        }else{
            remove();
            type.removed(this);
        }
    }

    @Override
    public void added(){
        Bullets type = (Bullets)this.type;

        facets.add(new BaseFacet(type.dark ? Sorter.dark : y - 10f, Sorter.object, p->{
            if(!type.dark) p.layer = y - 10f;
            type.draw(this);
        }));
    }

    @Override
    public void removed(){
        facets.free();
    }

    public Bullets type(){
        return (Bullets)type;
    }

}
