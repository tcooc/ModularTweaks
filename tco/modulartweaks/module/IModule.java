package tco.modulartweaks.module;

import net.minecraftforge.common.Property;
import tco.modulartweaks.ModularTweaksTransformer;

/**
 * A self-contained module that edits vanilla behaviour through base-edits,
 * reflection, and hooks.
 * 
 * initialize is called if the module is enabled
 * however, transform is always called since it occurs before classes are loaded
 * 
 * modules are always registered with the event bus (@Subscribe), but should only act if initialized
 * 
 * @author tcooc
 *
 */
public interface IModule {

	/**
	 * returns an array of properties
	 * with value=current value (default if initialize isn't called)
	 */
	public Property[] getConfig();

	/**
	 * @return non-null String
	 */
	public String getDescription();

	/**
	 * @return an unique String with no whitespace
	 */
	public String getName();

	public void initialize();

	/**
	 * Return false if setting fails
	 * @param key config name
	 * @param value value to set to
	 */
	public boolean setConfig(String key, String value);

	/**
	 * Does bytecode transformations, if necessary.
	 * @param name full name of the class being transformed
	 * @see ModularTweaksTransformer
	 */
	public void transform(ModularTweaksTransformer transformer, String name);
}
