package tco.modulartweaks;


public class CommonProxy {
	public void init() {
		ModularTweaks.instance.initializeActivatedModules(ModularTweaks.instance.commonModules);
	}
}
