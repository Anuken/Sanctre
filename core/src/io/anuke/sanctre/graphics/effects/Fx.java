package io.anuke.sanctre.graphics.effects;

import com.badlogic.gdx.graphics.Color;
import io.anuke.sanctre.graphics.DarkEffect;
import io.anuke.sanctre.graphics.DecalEffect;
import io.anuke.sanctre.graphics.SColors;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

public class Fx {

    public static final Effect

    swordspark = new Effect(7f, e -> {
        Angles.randLenVectors(e.id, 5, 25f * e.ifract(), e.rotation, 100f, (x, y) -> {
            float rad = e.fract() * 5f + 1f;
            Draw.color(Color.WHITE, Color.LIGHT_GRAY, e.ifract());
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
            Draw.reset();
        });
    }),
    dash = new Effect(8f, e -> {
        Draw.lineShot(e.x, e.y, e.rotation, 9, e.fract(), 42f, 1.6f, 0.88f);
        Draw.lineShot(e.x, e.y, e.rotation + 180f, 9, e.fract(), 7f, 2f, 0.88f);
    }),
    bloodspatter = new DecalEffect(2f, e -> {
        Draw.color(SColors.blood);

        Angles.randLenVectors(e.id, e.powfract(), 5, 6f, e.rotation, 100f, (x, y, f) -> {
            float rad = f * 6f + 3f;
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
        });

        Angles.randLenVectors(e.id, e.powfract(), 5, 20f, e.rotation, 100f, (x, y, f) -> {
            float rad = f * 5f;
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
        });

        Angles.randLenVectors(e.id, e.ifract(), 5, 13f, e.rotation, 100f, (x, y, f) -> {
            Draw.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), Mathf.dst(x, y));
        });

        Draw.reset();
    }),
    bloodparticle = new Effect(7f, e -> {
        Draw.color(SColors.blood);

        Angles.randLenVectors(e.id, 5, e.ifract() * 25f, e.rotation, 100f, (x, y) -> {
            float rad = 5f * e.fract() + 2f;
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
        });

        Draw.reset();
    }),
    sparkspatter = new DecalEffect(2f, e -> {
        Draw.color(SColors.blood);

        Draw.thick(2f);

        Angles.randLenVectors(e.id, e.ifract(), 5, 5f, e.rotation, 100f, (x, y, f) -> {
            Draw.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), 5f * f);
        });

        Draw.thick(1f);

        Angles.randLenVectors(e.id, e.ifract(), 7, 15f, e.rotation, 100f, (x, y, f) -> {
            Draw.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), 3f);
        });

        Draw.reset();
    }),
    sparkparticle = new Effect(8f, e -> {
        Draw.color(SColors.blood);

        Draw.thick(2f);

        Angles.randLenVectors(e.id, e.ifract(), 7, 20f, e.rotation, 100f, (x, y, f) -> {
            Draw.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), 5f * e.fract());
        });

        Draw.reset();
    }),
    hitparticle = new DarkEffect(7f, 1f, e -> {
        Draw.color(SColors.taint);
        Draw.lineShotFade(e.x, e.y, e.rotation, 9, e.fract(), 42f, 1.3f, 0.88f, 1f);
        Draw.reset();
    });
}
