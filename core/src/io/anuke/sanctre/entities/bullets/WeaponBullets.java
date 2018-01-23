package io.anuke.sanctre.entities.bullets;

import io.anuke.sanctre.entities.Bullet;
import io.anuke.sanctre.entities.BulletType;

public class WeaponBullets {
    public static final BulletType

    slash = new BulletType() {
        {
            lifetime = 8f;
            speed = 0.1f;
            hitsize = 13f;
            block = true;
        }

        @Override
        public void draw(Bullet b) {}
    };
}
