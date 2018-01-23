package io.anuke.sanctre.graphics.effects;

import io.anuke.sanctre.graphics.DarkEffect;
import io.anuke.sanctre.graphics.SColors;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

public class EnemyFx {
    public static final Effect

    darkparticle = new DarkEffect(8f, e -> {
        Draw.color(SColors.shade);

        Angles.randLenVectors(e.id, 6, e.ifract() * 27f, e.rotation, 100f, (x, y) -> {
            float rad = 6f * e.fract() + 2f;
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
        });

        Draw.lineShot(e.x, e.y, e.rotation, 9, e.fract(), 42f, 1.3f, 0.88f);

        Draw.reset();
    }),
    laserspark = new DarkEffect(12f, e -> {
        Draw.color(SColors.taint);
        Draw.thick(2f * e.fract());
        Draw.lineAngleCenter(e.x, e.y, e.rotation, 10f);
        Draw.reset();
    }),
    lasersparkthick = new DarkEffect(14f, e -> {
        Draw.color(SColors.taint);
        Draw.thick(1.5f * e.fract());
        Draw.lineAngleCenter(e.x, e.y, e.rotation, 12);
        Draw.thick(1f * e.fract());
        Draw.lineAngleCenter(e.x, e.y, e.rotation, 16f);
        Draw.reset();
    }),
    taintwave = new DarkEffect(8f, e -> {
        Draw.color(SColors.taint);
        Draw.polygon(e.x, e.y, 3, e.ifract() * 5f, e.rotation);
        Draw.thick(2f * e.fract());
    }),
    lasersine = new DarkEffect(70f, e -> {
        float length = 500f;
        float segment = 5f;

        float lastx = 0f, lasty = 0f;
        float sscl = 0f;//e.ifract();
        float tscl = Mathf.pow(e.fract(), 3f);

        for(int i = 0; i < 1; i ++) {
            Draw.color(i == 0 ? SColors.taint : SColors.taintLight, SColors.shade, e.ifract());
            float thickscl = i == 0 ? 1f : 0.5f;
            for (float f = segment; f < length; f += segment) {
                float fract = 1f - f / length;
                Tmp.v2.set(f, Mathf.sin(f, 8f, 30f * sscl * fract)).rotate(e.rotation);
                Draw.thick(fract * 10f * tscl * thickscl);
                Draw.line(e.x + lastx, e.y + lasty, e.x + Tmp.v2.x, e.y + Tmp.v2.y);
                lastx = Tmp.v2.x;
                lasty = Tmp.v2.y;
            }
            lastx = lasty = 0;
        }
        Draw.reset();
    }),
    lasercharge = new DarkEffect(40f, e -> {

        Draw.color(SColors.taintLight);
        Draw.thick(e.ifract() * 1f + 0.5f);

        Angles.randLenVectors(e.id, 7, 60f * e.fract(), (x, y) -> {
            //Draw.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), e.ifract() * 6f);
            //Draw.shapeCircle(e.x + x, e.y + y, e.ifract() * 3f);
            Draw.shape(e.x + x, e.y + y, 3, e.ifract() * 4f, Mathf.atan2(x, y) + 30 + 180f);
        });

        Draw.reset();
        /*
        Draw.color(SColors.taintLight);
        Draw.thick(e.ifract() * 2f + 0.5f);

        //Draw.spikes(e.x, e.y, e.fract() * 40f + 10f - e.ifract()*20f, -e.fract()*10f, 20, e.ifract()*360f);
        //Draw.swirl(e.x, e.y, e.fract() * 40f + 10f - e.ifract()*20f, e.fract() );

        Draw.reset();
        */
    }),
    laserprecharge = new DarkEffect(40f, e -> {
        Draw.color(SColors.taintLight);
        Draw.thick(e.ifract() * 3f);

        Draw.circle(e.x, e.y, e.fract() * 30f);
        Draw.spikes(e.x, e.y, e.fract() * 40f + 10f - e.ifract()*20f, -e.fract()*10f, 20);

        Angles.circleVectors(3, e.fract() * 60f, (x, y) -> {
            Draw.shape(e.x + x, e.y + y, 3, e.ifract() * 9f, Mathf.atan2(x, y) + 30 + 180f);
        });

        Draw.reset();
    });
}
