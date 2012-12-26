package tco.modulartweaks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

public class ObfuscationDecoder {
	private static final Map<String, String> obf = new HashMap<String, String>();

	public static String getObf(String key) {
		return obf.get(key);
	}

	public static String getCorrect(String key) {
		return isObfuscated() ? obf.get(key) : key;
	}

	public static boolean checkBoth(String key, String name) {
		return key.equals(name) || obf.get(key).equals(name);
	}

	public static boolean isObfuscated() {
		return !Block.class.getSimpleName().equals("Block");
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
		obf.put("canDropItself", "b");
		//net.minecraft.client.Minecraft.
		obf.put("guiAchievement", "u");
		//######## methods ########
		//net.minecraft.block.BlockGlass.
		obf.put("quantityDropped", "a");
		//net.minecraft.block.BlockLog.
		obf.put("breakBlock", "a");
		//net.minecraft.block.BlockCactus.
		obf.put("onEntityCollidedWithBlock", "a");
		//net.minecraft.world.Explosion.
		obf.put("doExplosionA", "a");
		obf.put("doExplosionB", "a");
		//net.minecraft.inventory.ContainerRepair.
		//net.minecraft.inventory.ContainerWorkbench.
		obf.put("canInteractWith", "a");
		//######## classes ########
		obf.put("net.minecraft.util.DamageSource", "lh");
		obf.put("net.minecraft.world.World", "yc");
		obf.put("net.minecraft.entity.Entity", "lq");
		obf.put("net.minecraft.entity.player.EntityPlayer", "qx");
		obf.put("net.minecraft.entity.item.EntityItem", "px");
		obf.put("net.minecraft.block.BlockGlass", "aki");
		obf.put("net.minecraft.block.BlockCactus", "ajg");
		obf.put("net.minecraft.block.BlockLog", "ana");
		obf.put("net.minecraft.world.Explosion", "xx");
		obf.put("net.minecraft.inventory.ContainerWorkbench", "rz");
		obf.put("net.minecraft.inventory.ContainerRepair", "sm");
	}
}
