package tco.modulartweaks;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for dealing with obfuscation.
 * @author tcooc
 */
public class ObfuscationDecoder {
	public static boolean obfuscation = false;

	private static final Map<String, String> obf = new HashMap<String, String>();

	/**
	 * Add mapping to the decoder
	 * @param key
	 * @param value
	 */
	public static void put(String key, String value) {
		obf.put(key, value);
	}

	/**
	 * @param key unobfuscated name
	 * @return obfuscated name
	 */
	public static String getObf(String key) {
		return obf.get(key);
	}

	/**
	 * @param key unobfuscated name
	 * @return correct name in current instance
	 */
	public static String getCorrect(String key) {
		return obfuscation ? obf.get(key) : key;
	}

	/**
	 * @param key unobfuscated name
	 * @return correct descriptor in current instance
	 */
	public static String getCorrectDesc(String key) {
		return getCorrect(key).replace('.', '/');
	}

	/**
	 * @param key reference value
	 * @param name value to check
	 * @return true if key equals name or unobfuscated key equals name
	 */
	public static boolean checkBoth(String key, String name) {
		return key.equals(name) || obf.get(key).equals(name);
	}

	protected static void dumpObfuscation() {
		StringBuffer sb = new StringBuffer();
		for(String key : obf.keySet()) {
			sb.append(key).append(' ');
		}
		ModularTweaks.logger.info(sb.toString());
	}

	static {
		//######## fields ########
		//net.minecraft.block.BlockPane.
		put("canDropItself", "b");
		//net.minecraft.client.Minecraft.
		put("guiAchievement", "u");
		//######## methods ########
		//net.minecraft.client.Minecraft.
		put("handleClientCommand", "aasdeeeeeeeeeeeeeeefix meeeeeeeeeeeeeee");
		//net.minecraft.block.BlockGlass.
		put("quantityDropped", "a");
		//net.minecraft.world.World.
		put("spawnEntityInWorld", "feeeeeeeeeeeeeeeeeeeeeeeedmmmmmmmmmmmeeeeeeeeeeeeeeeeeeeeeee");
		//net.minecraft.block.BlockLog.
		put("breakBlock", "a");
		//net.minecraft.block.BlockCactus.
		put("onEntityCollidedWithBlock", "a");
		//net.minecraft.world.Explosion.
		put("doExplosionA", "a");
		put("doExplosionB", "a");
		//net.minecraft.inventory.ContainerRepair.
		//net.minecraft.inventory.ContainerWorkbench.
		put("canInteractWith", "a");
		//######## classes ########
		put("net.minecraft.client.Minecraft", "fixxxx meeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		put("net.minecraft.util.DamageSource", "lh");
		put("net.minecraft.world.World", "yc");
		put("net.minecraft.entity.Entity", "lq");
		put("net.minecraft.entity.player.EntityPlayer", "qx");
		put("net.minecraft.entity.item.EntityItem", "px");
		put("net.minecraft.block.BlockGlass", "aki");
		put("net.minecraft.block.BlockCactus", "ajg");
		put("net.minecraft.block.BlockLog", "ana");
		put("net.minecraft.world.Explosion", "xx");
		put("net.minecraft.inventory.ContainerWorkbench", "rz");
		put("net.minecraft.inventory.ContainerRepair", "sm");
		put("net.minecraft.entity.item.EntityXPOrb", "umm, idk what the mappig for this is!!");
	}
}
