package tco.modulartweaks;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		super.init();
		for(IModule mod : ModularTweaks.instance.clientModules) {
			ModularTweaks.logger.info(mod.getName());
			mod.initialize();
		}
	}
}
