package io.anuke.sanctre.graphics.effects;

import com.badlogic.gdx.graphics.Color;
import io.anuke.sanctre.graphics.DarkEffect;
import io.anuke.sanctre.graphics.DecalEffect;
import io.anuke.sanctre.graphics.Palette;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.graphics.Shapes;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

public class Fx {

    public static final Effect

    swordspark = new Effect(7f, e -> {
        Angles.randLenVectors(e.id, 5, 25f * e.fin(), e.rotation, 100f, (x, y) -> {
            float rad = e.fout() * 5f + 1f;
            Draw.color(Color.WHITE, Color.LIGHT_GRAY, e.fin());
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
            Draw.reset();
        });
    }),
    dash = new Effect(8f, e -> {
        Shapes.lineShot(e.x, e.y, e.rotation, 9, e.fout(), 42f, 1.6f, 0.88f);
        Shapes.lineShot(e.x, e.y, e.rotation + 180f, 9, e.fout(), 7f, 2f, 0.88f);
    }),
    bloodspatter = new DecalEffect(2f, e -> {
        Draw.color(Palette.blood);

        Angles.randLenVectors(e.id, e.finpow(), 5, 6f, e.rotation, 100f, (x, y, f) -> {
            float rad = f * 6f + 3f;
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
        });

        Angles.randLenVectors(e.id, e.finpow(), 5, 20f, e.rotation, 100f, (x, y, f) -> {
            float rad = f * 5f;
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
        });

        Angles.randLenVectors(e.id, e.fin(), 5, 13f, e.rotation, 100f, (x, y, f) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), Mathf.dst(x, y));
        });

        Draw.reset();
    }),
    bloodparticle = new Effect(7f, e -> {
        Draw.color(Palette.blood);

        Angles.randLenVectors(e.id, 5, e.fin() * 25f, e.rotation, 100f, (x, y) -> {
            float rad = 5f * e.fout() + 2f;
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
        });

        Draw.reset();
    }),
    sparkspatter = new DecalEffect(2f, e -> {
        Draw.color(Palette.blood);

        Lines.stroke(2f);

        Angles.randLenVectors(e.id, e.fin(), 5, 5f, e.rotation, 100f, (x, y, f) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), 5f * f);
        });

        Lines.stroke(1f);

        Angles.randLenVectors(e.id, e.fin(), 7, 15f, e.rotation, 100f, (x, y, f) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), 3f);
        });

        Draw.reset();
    }),
    sparkparticle = new Effect(8f, e -> {
        Draw.color(Palette.blood);

        Lines.stroke(2f);

        Angles.randLenVectors(e.id, e.fin(), 7, 20f, e.rotation, 100f, (x, y, f) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), 5f * e.fout());
        });

        Draw.reset();
    }),
    hitparticle = new DarkEffect(7f, 1f, e -> {
        Draw.color(Palette.taint);
        Shapes.lineShotFade(e.x, e.y, e.rotation, 9, e.fout(), 42f, 1.3f, 0.88f, 1f);
        Draw.reset();
    });
}
