package io.anuke.sanctre.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.entities.EffectEntity;
import io.anuke.ucore.facet.BaseFacet;
import io.anuke.ucore.facet.Facets;
import io.anuke.ucore.facet.Sorter;

public class FacetEffect extends EffectEntity {
    private BaseFacet facet;

    public FacetEffect(Effect effect, Color color, float rotation) {
        super(effect, color, rotation);
    }

    @Override
    public void added(){
        draw();
    }

    @Override
    public void removed(){
        facet.remove();
    }

    @Override
    public void draw(){
        facet = new BaseFacet(y - 20f, Sorter.object, d -> {
            Effects.renderEffect(id, renderer, color, time, rotation, x, y);
        });
        Facets.instance().add(facet);
    }

}
