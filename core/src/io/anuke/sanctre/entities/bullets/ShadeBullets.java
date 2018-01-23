package io.anuke.sanctre.entities.bullets;

import io.anuke.sanctre.entities.Bullet;
import io.anuke.sanctre.entities.BulletType;
import io.anuke.sanctre.graphics.SColors;
import io.anuke.sanctre.graphics.effects.EnemyFx;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

public class ShadeBullets {
    public static final BulletType

    laser = new BulletType() {
        {
            line = true;
            length = 400f;
            lifetime = 10f;
            speed = 0.01f;
            dark = true;
            damage = 2;
            lineEffect = EnemyFx.laserspark;
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
    },
    biglaser = new BulletType() {
        {
            line = true;
            length = 500f;
            lifetime = 20f;
            speed = 0.01f;
            dark = true;
            damage = 3;
            lineEffect = EnemyFx.lasersparkthick;
        }

        @Override
        public void draw(Bullet b) {
            Draw.color(SColors.taint);

            float f = b.fract()*5f;

            Draw.rect("circle", b.x, b.y, 6f*f, 6f*f);
            Draw.thick(3f * f);
            Draw.lineAngle(b.x, b.y, b.angle(), length);

            Draw.thick(2f * f);
            Draw.lineAngle(b.x, b.y, b.angle(), length + 8f);
            Draw.thick(1f * f);
            Draw.lineAngle(b.x, b.y, b.angle(), length + 16f);

            Draw.color(SColors.taintLight, SColors.taint, b.ifract() > 0.5f ? 1f : 0f);

            float pf = Mathf.pow(b.fract(), 3f);

            for(int i : Mathf.signs){
                Draw.lineShotFade(b.x, b.y, b.angle() + 45 * i, 5, pf, 50f, 1.5f, 0.8f, 1f);
                Draw.lineShotFade(b.x, b.y, b.angle() + 135 * i, 5, pf, 50f, 1.5f, 0.8f, 1f);
                //Draw.lineShotFade(b.x, b.y, b.angle() + 15 * i, 3, b.fract(), 80f, 2f, 0.5f);
            }

            Draw.color(SColors.taintLight);
            Draw.thick(1.5f * f);
            Draw.rect("circle", b.x, b.y, 4f*f, 4f*f);
            Draw.lineAngle(b.x, b.y, b.angle(), length);

            Draw.reset();
        }
    },
    orb = new BulletType() {
        {
            lifetime = 110f;
            speed = 4f;
            dark = true;
            despawneffect = EnemyFx.taintwave;
        }

        @Override
        public void draw(Bullet b) {
            Draw.color(SColors.taint);
            Draw.thick(2f);
            Draw.polygon(b.x, b.y, 3, 4f, b.angle() + 30);
            Draw.reset();
        }
    },
    ball = new BulletType() {
        {
            lifetime = 50f;
            speed = 4f;
            dark = true;
            despawneffect = EnemyFx.taintwave;
        }

        @Override
        public void draw(Bullet b) {
            Draw.color(SColors.taint);
            Draw.thick(2f);
            Draw.circle(b.x, b.y, 6f);
            Draw.reset();
        }

        @Override
        public void removed(Bullet b) {
            Effects.shake(3f, 3f, b);
            Angles.circle(5, f -> {
                new Bullet(orb, b.owner, b.x, b.y, f + b.angle()).add();
            });
        }

        @Override
        public void despawned(Bullet b) {
            removed(b);
        }
    },
    lineb = new BulletType() {
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
    };
}
