package tco.modulartweaks;

public class CommonProxy {
	public void init() {
		for(Module mod : ModularTweaks.serverModules) {
			ModularTweaks.logger.info(mod.getName());
			mod.initialize();
		}
	}
}
