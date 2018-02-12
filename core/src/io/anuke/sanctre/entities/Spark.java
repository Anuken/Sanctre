package io.anuke.sanctre.entities;

import com.badlogic.gdx.math.Vector2;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.DestructibleEntity;
import io.anuke.ucore.facet.BaseFacet;
import io.anuke.ucore.facet.BaseFacet.DrawFunc;
import io.anuke.ucore.facet.FacetList;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.function.Listenable;
import io.anuke.ucore.function.Supplier;
import io.anuke.ucore.util.Translator;

public class Spark extends DestructibleEntity {
    public FacetList facets = new FacetList();
    public float height = 5f;
    public Vector2 velocity = new Vector2();
    public float drag = 0.4f;

    protected Translator tr = new Translator();
    protected Vector2 vector = new Vector2();

    public Spark(){
        hitboxTile.offsety = 1f;
        hitboxTile.setSize(4f, 2f);
        hitbox.offsety = 3f;
    }

    @Override
    public void added(){
        draw();
    }

    @Override
    public void removed(){
        facets.free();
    }

    public void impulse(float x, float y){
        velocity.add(x, y);
    }

    public void impulse(Vector2 v){
        velocity.add(v);
    }

    public Vector2 updateVelocity(){
        float px = x, py = y;
        move(velocity.x * Timers.delta(), velocity.y * Timers.delta());
        velocity.scl(1f-drag * Timers.delta());
        return vector.set(x - px, y - py);
    }

    public void shoot(BulletType type, float angle){
        shoot(type, x, y + height, angle);
    }

    public void shoot(BulletType type, float x, float y, float angle){
        new Bullet(type, this, x, y, angle).add();
    }

    public void draw(Sorter sorter, float layer, Listenable l){
        facets.add(new BaseFacet(layer, sorter, p -> l.listen()));
    }

    public void draw(int shadowsize, Listenable d){
        drawShadow(shadowsize, 0);
        facets.add(new BaseFacet(0, Sorter.object, p->{
            p.layer = y;
            d.listen();
        }));
    }

    public void draw(int shadowsize, Supplier<Float> layer, Listenable d){
        drawShadow(shadowsize, 0);
        facets.add(new BaseFacet(0, Sorter.object, p->{
            p.layer = layer.get();
            d.listen();
        }));
    }

    public void draw(DrawFunc d){
        facets.add(new BaseFacet(0, Sorter.object, d));
    }

    public void shadow(int size){
        drawShadow(size, 0);
    }

    public void drawShadow(int size, float offsety){
        drawShadow(size, offsety, false);
    }

    public void drawShadow(int size, float offsety, boolean round){

        String shadow = "shadow"
                + (int) (size * 0.8f / 2f + Math.pow(size, 1.5f) / 200f) * 2;

        draw(p -> {
            Draw.color();
            p.provider = Sorter.tile;
            p.layer = Sorter.shadow;

            if(!round){
                Draw.rect(shadow, x, y + offsety);
            }else{
                Draw.rect(shadow, (int)x, (int)y + offsety);
            }
        });
    }
}
