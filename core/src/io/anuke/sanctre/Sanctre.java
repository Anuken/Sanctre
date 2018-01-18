package io.anuke.sanctre;

import io.anuke.ucore.modules.ModuleCore;

public class Sanctre extends ModuleCore {
	
	@Override
	public void init(){
		module(Vars.control = new Control());
		module(Vars.renderer = new Renderer());
		module(Vars.world = new World());
		module(Vars.ui = new UI());
	}
	
}
