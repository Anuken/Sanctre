package io.anuke.sanctre.graphics;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.util.Angles;

public class Fx {
    public static final Effect

    swordspark = new Effect(7f, e -> {
        Angles.randLenVectors(e.id, 5, 25f * e.ifract(), (x, y) -> {
            Angles.translation(e.rotation, 2f);
            float rad = e.fract() * 5f + 1f;
            Draw.color(Color.WHITE, Color.LIGHT_GRAY, e.ifract());
            Draw.rect("circle", e.x + x, e.y + y, rad, rad);
            Draw.reset();
        });
    }),
    dash = new Effect(8f, e -> {
        Draw.lineShot(e.x, e.y, e.rotation, 9, e.fract(), 42f, 1.6f, 0.88f);
        Draw.lineShot(e.x, e.y, e.rotation + 180f, 9, e.fract(), 7f, 2f, 0.88f);
    });
}
