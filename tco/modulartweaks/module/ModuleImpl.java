package tco.modulartweaks.module;

import net.minecraftforge.common.Property;
import tco.modulartweaks.ModularTweaksTransformer;

public abstract class ModuleImpl implements IModule {
	public static final Property[] NONE = new Property[0];

	@Override
	public Property[] getConfig() {
		return NONE;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public void initialize() {
	}

	@Override
	public boolean setConfig(String key, String value) {
		return false;
	}

	@Override
	public void transform(ModularTweaksTransformer transformer, String name) {
	}

}
