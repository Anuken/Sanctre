package io.anuke.sanctre.graphics;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ucore.graphics.Hue;

public class SColors {
    public static final Color
    shade = Color.valueOf("3f3f3f"),
    shadeDark = Hue.lightness(0.1f),
    taint = Color.valueOf("882ddc"),
    taintLight = Color.valueOf("b566ff"),
    blood = Hue.lightness(0.3f);
}
