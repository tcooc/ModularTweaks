package tco.modulartweaks;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		super.init();
		for(Module mod : ModularTweaks.clientModules) {
			ModularTweaks.logger.info(mod.getName());
			mod.initialize();
		}
	}
}
