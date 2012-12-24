package tco.modulartweaks;

import tco.modulartweaks.module.IModule;

public class CommonProxy {
	public void init() {
		for(IModule mod : ModularTweaks.instance.commonModules) {
			ModularTweaks.logger.info(mod.getName());
			mod.initialize();
		}
	}
}
