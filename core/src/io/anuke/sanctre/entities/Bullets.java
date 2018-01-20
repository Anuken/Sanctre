package io.anuke.sanctre.entities;

import io.anuke.sanctre.graphics.Fx;
import io.anuke.sanctre.graphics.SColors;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.entities.BaseBulletType;
import io.anuke.ucore.util.Mathf;

public abstract class Bullets extends BaseBulletType<Bullet> {
    public static final Bullets

    test = new Bullets() {
        {
            lifetime = 10f;
            speed = 1f;
            block = true;
        }
        @Override
        public void draw(Bullet b) {
            Draw.lineShot(b.x, b.y, b.angle(), 9, b.fract(), 42f, 1.6f, 0.88f);
            Draw.lineShot(b.x, b.y, b.angle() + 180f, 9, b.fract(), 7f, 2f, 0.88f);
            Draw.reset();
        }
    },
    orb = new Bullets() {
        {
            lifetime = 120f;
            speed = 3f;
            dark = true;
        }

        @Override
        public void draw(Bullet b) {
            Draw.color(SColors.taint);
            Draw.circle(b.x, b.y, 5f);
            Draw.reset();
        }
    },
    lineb = new Bullets() {
        {
            lifetime = 120f;
            speed = 4f;
            dark = true;
        }

        @Override
        public void draw(Bullet b) {
            Draw.color(SColors.taint);
            Draw.lineAngleCenter(b.x, b.y, b.angle(), 7f);
            Draw.thick(2f);
            Draw.lineAngleCenter(b.x, b.y, b.angle(), 5f);
            Draw.reset();
        }
    },
    phase = new Bullets() {
        {
            lifetime = 20f;
            speed = 4.5f;
            drag = 0.07f;
        }

        @Override
        public void draw(Bullet b) {
            Draw.lineShot(b.x, b.y, b.angle(), 4, b.fract(), 20f, 1.5f, 0.8f);
            Draw.lineShot(b.x, b.y, b.angle() + 180, 4, b.fract(), 20f, 1.5f, 0.8f);
            //Draw.polygon(b.x, b.y, 3, 3f, b.angle() - 90);
            Draw.reset();
        }
    },
    slash = new Bullets() {
        {
            lifetime = 8f;
            speed = 0.1f;
            hitsize = 13f;
            block = true;
        }

        @Override
        public void draw(Bullet b) {}
    },
    laser = new Bullets() {
        {
            line = true;
            length = 400f;
            lifetime = 10f;
            speed = 0.01f;
            dark = true;
            damage = 2;
        }

        @Override
        public void draw(Bullet b) {
            Draw.color(SColors.taint);

            float f = b.fract()*2f;

            Draw.rect("circle", b.x, b.y, 6f*f, 6f*f);
            Draw.thick(3f * f);
            Draw.lineAngle(b.x, b.y, b.angle(), length);

            Draw.thick(2f * f);
            Draw.lineAngle(b.x, b.y, b.angle(), length + 6f);
            Draw.thick(1f * f);
            Draw.lineAngle(b.x, b.y, b.angle(), length + 12f);

            Draw.color(SColors.taintLight, SColors.taint, b.ifract());

            for(int i : Mathf.signs){
                Draw.lineShot(b.x, b.y, b.angle() + 70 * i, 3, b.fract(), 12f, 2f, 0.5f);
                Draw.lineShot(b.x, b.y, b.angle() + 30 * i, 3, b.fract(), 30f, 1.3f, 0.5f);
            }

            Draw.color(SColors.taintLight);
            Draw.thick(1.5f * f);
            Draw.rect("circle", b.x, b.y, 3f*f, 3f*f);
            Draw.lineAngle(b.x, b.y, b.angle(), length);

            Draw.reset();
        }
    };

    public boolean dark;
    public boolean block;
    public Effect hitEffect = Fx.swordspark;
    public boolean line;
    public float length = 100f;
    public Effect lineEffect = Fx.laserspark;
    public int effects = 30;

}
