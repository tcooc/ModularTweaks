package tco.modulartweaks;

public class CommonProxy {
	public void init() {
		for(IModule mod : ModularTweaks.instance.commonModules) {
			ModularTweaks.logger.info(mod.getName());
			mod.initialize();
		}
	}
}
