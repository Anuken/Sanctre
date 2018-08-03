package io.anuke.sanctre.graphics.effects;

import io.anuke.sanctre.graphics.DarkEffect;
import io.anuke.sanctre.graphics.Palette;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.graphics.Shapes;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Translator;

public class EnemyFx {
    private static final Translator tr = new Translator();

    public static final Effect

    darkparticle = new DarkEffect(8f, e -> {
        Draw.color(Palette.shade);

        Angles.randLenVectors(e.id, 6, e.fin() * 27f, e.rotation, 100f, (x, y) -> {
            float rad = 6f * e.fout() + 2f;
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
        });

        Shapes.lineShot(e.x, e.y, e.rotation, 9, e.fout(), 42f, 1.3f, 0.88f);

        Draw.reset();
    }),
    laserspark = new DarkEffect(12f, e -> {
        Draw.color(Palette.taint);
        Lines.stroke(2f * e.fout());
        Lines.lineAngleCenter(e.x, e.y, e.rotation, 10f);
        Draw.reset();
    }),
    lasersparkthick = new DarkEffect(14f, e -> {
        Draw.color(Palette.taint);
        Lines.stroke(1.5f * e.fout());
        Lines.lineAngleCenter(e.x, e.y, e.rotation, 12);
        Lines.stroke(1f * e.fout());
        Lines.lineAngleCenter(e.x, e.y, e.rotation, 16f);
        Draw.reset();
    }),
    taintwave = new DarkEffect(8f, e -> {
        Draw.color(Palette.taint);
        Lines.stroke(2f * e.fout());
        Lines.poly(e.x, e.y, 3, e.fin() * 5f, e.rotation);
        Draw.reset();
    }),
    taintwavel = new DarkEffect(12f, e -> {
        Draw.color(Palette.taintLight);
        Lines.stroke(2f * e.fout());
        Lines.poly(e.x, e.y, 3, e.fin() * 15f + 14f, e.rotation);
        Draw.reset();
    }),
    lasersine = new DarkEffect(70f, e -> {
        float length = 500f;
        float segment = 5f;

        float lastx = 0f, lasty = 0f;
        float sscl = e.fin() / 14f;
        float tscl = Mathf.pow(e.fout(), 3f);

        Draw.color(Palette.taint, Palette.shade, e.fin());
        float thickscl = 1f;
        for (float f = segment; f < length; f += segment) {
            float fract = 1f - f / length;

            tr.trns(e.rotation, f, Mathf.sin(f, 8f, 30f * sscl * fract));

            Lines.stroke(fract * 10f * tscl * thickscl);
            Lines.line(e.x + lastx, e.y + lasty, e.x + tr.x, e.y + tr.y);
            lastx = tr.x;
            lasty = tr.y;
        }
        Draw.reset();
    }),
    laserin = new DarkEffect(70f, e -> {
        float length = 500f;
        float segment = 5f;

        float lastx = 0f, lasty = 0f;
        float sscl = 0f;
        float tscl = Mathf.pow(e.fin(), 1f);

        Draw.color(Palette.taint, Palette.shade, e.fout());
        float thickscl = 1f;
        for (float f = segment; f < length; f += segment) {
            float fract = 1f - f / length;

            tr.trns(e.rotation, f, Mathf.sin(f, 8f, 30f * sscl * fract));

            Lines.stroke(fract* tscl * thickscl);
            Lines.line(e.x + lastx, e.y + lasty, e.x + tr.x, e.y + tr.y);
            lastx = tr.x;
            lasty = tr.y;
        }
        Draw.reset();
    }),
    shadeshoot = new DarkEffect(7f, e -> {
        Draw.color(Palette.taint, Palette.taintLight, e.fout() > 0.5f ? 1f : 0f);

        Shapes.lineShot(e.x, e.y, e.rotation, 6, e.fout(), 30f, 2f, 0.8f);
        Fill.circle(e.x, e.y, e.fout() * 5f);
        Draw.reset();
    }),
    lasercharge = new DarkEffect(40f, e -> {

        Draw.color(Palette.taintLight);
        Lines.stroke(e.fin() * 1f + 0.5f);

        Angles.randLenVectors(e.id, 7, 60f * e.fout(), (x, y) -> {
            Fill.poly(e.x + x, e.y + y, 3, e.fin() * 4f, Mathf.atan2(x, y) + 30);
        });

        Draw.reset();
    }),
    laserprecharge = new DarkEffect(40f, e -> {
        Draw.color(Palette.taintLight);
        Lines.stroke(e.fin() * 3f);

        Lines.circle(e.x, e.y, e.fout() * 30f);
        Lines.spikes(e.x, e.y, e.fout() * 40f + 10f - e.fin()*20f, -e.fout()*10f, 20);

        Angles.circleVectors(3, e.fout() * 60f, (x, y) -> {
            Fill.poly(e.x + x, e.y + y, 3, e.fin() * 9f, Mathf.atan2(x, y) + 30);
        });

        Draw.reset();
    });
}
