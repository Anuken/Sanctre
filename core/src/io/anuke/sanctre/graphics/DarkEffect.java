package io.anuke.sanctre.graphics;

import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.function.EffectRenderer;

public class DarkEffect extends Effect {
    public float shift;

    public DarkEffect(float life, EffectRenderer draw) {
        super(life, draw);
    }

    public DarkEffect(float life, float shift, EffectRenderer draw) {
        super(life, draw);
        this.shift = shift;
    }
}
