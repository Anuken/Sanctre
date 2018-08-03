package io.anuke.sanctre.entities;

import io.anuke.sanctre.Vars;
import io.anuke.sanctre.graphics.DarkEffect;
import io.anuke.sanctre.graphics.DecalEffect;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.entities.impl.EffectEntity;
import io.anuke.ucore.facet.BaseFacet;
import io.anuke.ucore.facet.Facets;
import io.anuke.ucore.facet.Sorter;

public class FacetEffect extends EffectEntity{
    private BaseFacet facet;

    @Override
    public void added(){
        draw();
    }

    @Override
    public void removed(){
        if(effect instanceof DecalEffect){
            time = lifetime();
            Vars.renderer.decals().addDraw(() -> Effects.renderEffect(id, effect, color, time, rotation, x, y, data));
        }
        facet.remove();
    }

    @Override
    public void draw(){
        facet = new BaseFacet(effect instanceof DarkEffect ? Sorter.dark + ((DarkEffect)effect).shift : y - 20f,
                effect instanceof DecalEffect ? Sorter.tile : Sorter.object, d -> {
            Effects.renderEffect(id, effect, color, time, rotation, x, y, data);
        });
        Facets.instance().add(facet);
    }

    @Override
    public void reset() {
        facet = null;
    }
}
