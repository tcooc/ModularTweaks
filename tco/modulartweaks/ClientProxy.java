package tco.modulartweaks;


public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		super.init();
		ModularTweaks.instance.initializeActivatedModules(ModularTweaks.instance.clientModules);
	}
}
