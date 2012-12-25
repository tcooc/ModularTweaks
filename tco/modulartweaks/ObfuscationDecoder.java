package tco.modulartweaks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

public class ObfuscationDecoder {
	private static final Map<String, String> obf = new HashMap<String, String>();

	public static String get(String key) {
		return obf.get(key);
	}

	public static boolean checkBoth(String key, String name) {
		return key.equals(name) || obf.get(key).equals(name);
	}

	public static boolean isObfuscated() {
		return !Block.class.getSimpleName().equals("Block");
	}

	static {
		obf.put("canDropItself", "b");
		obf.put("quantityDropped", "a");
		obf.put("breakBlock", "a");
		obf.put("onEntityCollidedWithBlock", "a");
		obf.put("doExplosionA", "N//A");
		obf.put("doExplosionB", "N//A");
		obf.put("canInteractWith", "B/a");
		obf.put("net.minecraft.block.BlockGlass", "aki");
		obf.put("net.minecraft.block.BlockCactus", "ajg");
		obf.put("net.minecraft.block.BlockLog", "ana");
		obf.put("net.minecraft.world.Explosion", "anfjlksfkdsal;dka;lsdk;akd;laskd;sk;sakdsl");
		obf.put("net.minecraft.inventory.ContainerWorkbench", "workhello");
	}
}
