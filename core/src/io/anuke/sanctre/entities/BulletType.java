package io.anuke.sanctre.entities;

import io.anuke.sanctre.graphics.effects.Fx;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.entities.impl.BaseBulletType;

public abstract class BulletType extends BaseBulletType<Bullet>{
    public boolean dark;
    public boolean block;
    public Effect hitEffect = Fx.swordspark;
    public boolean line;
    public float length = 100f;
    public Effect lineEffect;
    public int effects = 30;
}
