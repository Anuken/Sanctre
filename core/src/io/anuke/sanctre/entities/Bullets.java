package io.anuke.sanctre.entities;

import io.anuke.sanctre.graphics.Fx;
import io.anuke.sanctre.graphics.SColors;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.entities.BaseBulletType;

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
            speed = 2f;
            dark = true;
        }

        @Override
        public void draw(Bullet b) {
            Draw.color(SColors.taint);
            Draw.circle(b.x, b.y, 5f);
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
    };

    public boolean dark;
    public boolean block;
    public Effect hitEffect = Fx.swordspark;

}
