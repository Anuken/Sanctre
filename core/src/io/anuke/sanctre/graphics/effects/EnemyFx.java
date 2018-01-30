package io.anuke.sanctre.graphics.effects;

import io.anuke.sanctre.graphics.DarkEffect;
import io.anuke.sanctre.graphics.SColors;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.graphics.Shapes;
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

        Shapes.lineShot(e.x, e.y, e.rotation, 9, e.fract(), 42f, 1.3f, 0.88f);

        Draw.reset();
    }),
    laserspark = new DarkEffect(12f, e -> {
        Draw.color(SColors.taint);
        Lines.stroke(2f * e.fract());
        Lines.lineAngleCenter(e.x, e.y, e.rotation, 10f);
        Draw.reset();
    }),
    lasersparkthick = new DarkEffect(14f, e -> {
        Draw.color(SColors.taint);
        Lines.stroke(1.5f * e.fract());
        Lines.lineAngleCenter(e.x, e.y, e.rotation, 12);
        Lines.stroke(1f * e.fract());
        Lines.lineAngleCenter(e.x, e.y, e.rotation, 16f);
        Draw.reset();
    }),
    taintwave = new DarkEffect(8f, e -> {
        Draw.color(SColors.taint);
        Lines.poly(e.x, e.y, 3, e.ifract() * 5f, e.rotation);
        Lines.stroke(2f * e.fract());
    }),
    lasersine = new DarkEffect(70f, e -> {
        float length = 500f;
        float segment = 5f;

        float lastx = 0f, lasty = 0f;
        float sscl = 0f;//e.ifract();
        float tscl = Mathf.pow(e.fract(), 3f);

        Draw.color(SColors.taint, SColors.shade, e.ifract());
        float thickscl = 1f;
        for (float f = segment; f < length; f += segment) {
            float fract = 1f - f / length;
            Tmp.v2.set(f, Mathf.sin(f, 8f, 30f * sscl * fract)).rotate(e.rotation);
            Lines.stroke(fract * 10f * tscl * thickscl);
            Lines.line(e.x + lastx, e.y + lasty, e.x + Tmp.v2.x, e.y + Tmp.v2.y);
            lastx = Tmp.v2.x;
            lasty = Tmp.v2.y;
        }
        Draw.reset();
    }),
    laserin = new DarkEffect(70f, e -> {
        float length = 500f;
        float segment = 5f;

        float lastx = 0f, lasty = 0f;
        float sscl = 0f;
        float tscl = Mathf.pow(e.ifract(), 1f);

        Draw.color(SColors.taint, SColors.shade, e.fract());
        float thickscl = 1f;
        for (float f = segment; f < length; f += segment) {
            float fract = 1f - f / length;
            Tmp.v2.set(f, Mathf.sin(f, 8f, 30f * sscl * fract)).rotate(e.rotation);
            Lines.stroke(fract* tscl * thickscl);
            Lines.line(e.x + lastx, e.y + lasty, e.x + Tmp.v2.x, e.y + Tmp.v2.y);
            lastx = Tmp.v2.x;
            lasty = Tmp.v2.y;
        }
        Draw.reset();
    }),
    shadeshoot = new DarkEffect(7f, e -> {
        Draw.color(SColors.taint, SColors.taintLight, e.fract() > 0.5f ? 1f : 0f);

        Shapes.lineShot(e.x, e.y, e.rotation, 6, e.fract(), 30f, 2f, 0.8f);
        Fill.circle(e.x, e.y, e.fract() * 5f);
        Draw.reset();
    }),
    lasercharge = new DarkEffect(40f, e -> {

        Draw.color(SColors.taintLight);
        Lines.stroke(e.ifract() * 1f + 0.5f);

        Angles.randLenVectors(e.id, 7, 60f * e.fract(), (x, y) -> {
            Fill.poly(e.x + x, e.y + y, 3, e.ifract() * 4f, Mathf.atan2(x, y) + 30 + 180f);
        });

        Draw.reset();
    }),
    laserprecharge = new DarkEffect(40f, e -> {
        Draw.color(SColors.taintLight);
        Lines.stroke(e.ifract() * 3f);

        Lines.circle(e.x, e.y, e.fract() * 30f);
        Lines.spikes(e.x, e.y, e.fract() * 40f + 10f - e.ifract()*20f, -e.fract()*10f, 20);

        Angles.circleVectors(3, e.fract() * 60f, (x, y) -> {
            Fill.poly(e.x + x, e.y + y, 3, e.ifract() * 9f, Mathf.atan2(x, y) + 30 + 180f);
        });

        Draw.reset();
    });
}
