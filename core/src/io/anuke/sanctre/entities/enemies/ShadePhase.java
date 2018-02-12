package io.anuke.sanctre.entities.enemies;

import io.anuke.sanctre.entities.bullets.ShadeBullets;
import io.anuke.sanctre.graphics.effects.EnemyFx;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

import static io.anuke.ucore.core.Timers.get;
import static io.anuke.ucore.core.Timers.getTime;
import static io.anuke.ucore.core.Timers.run;
import static io.anuke.ucore.util.Angles.circle;

public enum ShadePhase {
    arms{
        void update(Shade s){
            s.armsTo(Mathf.absin(Timers.time(), 10f, 184f) - 5f);
            if(get(s, "fsh", 5)){
                s.shootArms(ShadeBullets.ball);
            }
        }
    },
    tris{
        float reload = 60;
        {
            rotateMin = false;
            rotate = false;
        }

        void update(Shade s) {
            s.trirot += Timers.delta() * 3f;

            s.tris(tri -> {
                tri.length = Mathf.lerpDelta(tri.length, getTime(tri, "laser")/reload * 80f, 0.1f);
                tri.angle = Mathf.lerpAngDelta(tri.angle, s.target.angleTo(tri.x, tri.y) + 180f, 0.8f);
                tri.charge = getTime(tri, "laser") / reload;

                if (get(tri, "laser", reload)) {
                    Effects.shake(5f, 5f, tri.x, tri.y);
                    s.shoot(ShadeBullets.laser, tri.x + Angles.trnsx(tri.angle, 1f), tri.y + Angles.trnsx(tri.angle, 1f), tri.angle);
                    Effects.effect(EnemyFx.taintwavel, tri.x, tri.y, tri.angle);
                }
            });

            s.armsTo(Mathf.absin(Timers.time(), 9f, 184f) - 5f);

            if(get(s, "fsh", 5)){
                s.shootArms(ShadeBullets.ball);
            }
        }

        void begin(Shade s) {
            for (int i = 0; i < 3; i++) {
                Timers.reset(s.tris[i], "laser", reload/3*i);
            }
        }
    },
    circle{
        float angle = 0f;
        float speed = 0f;
        float rev = 1f;

        void update(Shade s){
            if(get(s, "dash", 15 - speed)){
                circle(3, f -> s.shoot(ShadeBullets.laser, angle + f));
                angle += 15 - speed/2f;
                speed += 0.7f - speed / 15f;
            }

            if(speed > 9f && rev > 0){
                rev = -1f;

                circle(20, f -> s.shoot(ShadeBullets.ball, f));
            }
        }

        void end(Shade s){
            speed = 0f;
            rev = 1f;
        }
    },
    laser{
        float time = 0f;
        float reload = 50f;
        float angle = 0f;
        boolean shot = false;

        void update(Shade s){
            if(time < reload){
                if(!shot && time > reload - 20){
                    angle = s.angleTo();
                    shot = true;
                }
                time += Timers.delta();

                if(time > 10f && get(s, "lfx1", 10)){
                    s.effect(EnemyFx.lasercharge, 0);
                }

                if(time > reload - 40f && get(s, "lfx2", reload)){
                    s.effect(EnemyFx.laserprecharge, 0);
                }
            }else{
                s.effect(EnemyFx.lasersine, angle);
                s.shoot(ShadeBullets.biglaser, angle);
                Effects.shake(12f, 13f, s.x, s.y);
                time = 0f;
                shot = false;
            }

            s.shootTime = time / reload;
        }
    },
    balls{
        float angle = 0f;

        void update(Shade s){

            if(get(s, "balls", 60)){
                for(int i =0; i < 8; i ++){
                    run(2.7f * i, () -> {
                        circle(3, c -> {
                            s.shoot(ShadeBullets.ball, c + angle);
                        });

                        angle += 16f;
                    });
                }
            }
        }
    };
    boolean rotate = true;
    boolean rotateMin = true;

    abstract void update(Shade s);
    void begin(Shade s){}
    void end(Shade s){}
}
