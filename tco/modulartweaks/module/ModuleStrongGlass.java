package tco.modulartweaks.module;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ModuleStrongGlass extends ModuleImpl {

	private static boolean dropPane = false;
	private static double glassDropChance = 0.0;

	public static int onGlassBreak(Random rand) {
		return rand.nextDouble() < glassDropChance ? 1 : 0;
	}

	@Override
	public String getDescription() {
		return "Change glass and glas pane drop behaviour.";
	}

	@Override
	public String getName() {
		return "StrongGlass";
	}

	@Override
	public void initialize() {
		if(dropPane) {
			ObfuscationReflectionHelper.setPrivateValue(
					BlockPane.class, (BlockPane) Block.thinGlass, true,
					"canDropItself", ObfuscationDecoder.getObf("canDropItself"));
		}
	}

	@Override
	public Property[] getConfig() {
		Property[] config = new Property[2];
		config[0] = new Property("dropPane", String.valueOf(dropPane), Type.BOOLEAN);
		config[0].comment = "Drop glass panes";
		config[1] = new Property("glassDropChance", String.valueOf(glassDropChance), Type.DOUBLE);
		config[1].comment = "Chance of dropping glass blocks, 1.0=100%, 0.0=0%";
		return config;
	}

	@Override
	public boolean setConfig(String key, String value) {
		if("dropPane".equals(key)) {
			return true;
		}
		if("glassDropChance".equals(key)) {
			return true;
		}
		return false;
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		if(ObfuscationDecoder.checkBoth("net.minecraft.block.BlockGlass", name)) {
			trans.startTransform();
			String signature = "(Ljava/util/Random;)I";
			MethodNode method = trans.findMethod("quantityDropped", signature);
			InsnList insn = new InsnList();
			insn.add(new VarInsnNode(ALOAD, 1));
			insn.add(new MethodInsnNode(INVOKESTATIC, getClass().getCanonicalName().replace('.', '/'),
					"onGlassBreak", signature));
			insn.add(new InsnNode(IRETURN));
			method.instructions = insn;
			trans.stopTransform();
		}
	}
}
