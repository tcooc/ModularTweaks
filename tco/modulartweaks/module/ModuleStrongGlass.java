package tco.modulartweaks.module;

import java.lang.reflect.Method;
import java.util.Random;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ModuleStrongGlass implements IModule {

	private static boolean dropPane = true;
	private static double glassDropChance = 1.0;

	@Override
	public void initialize() {
		if(dropPane) {
			ObfuscationReflectionHelper.setPrivateValue(
					BlockPane.class, (BlockPane) Block.thinGlass, true,
					"canDropItself", ObfuscationDecoder.get("canDropItself"));
		}
	}

	@Override
	public String getName() {
		return "Strong Glass";
	}

	@Override
	public String getDescription() {
		return "Change glass and glas pane drop behaviour.";
	}

	@Override
	public void loadConfigs(Configuration config) {
		dropPane = config.get(getName(), "dropPane", dropPane,
				"Drop glass panes").getBoolean(dropPane);
		glassDropChance = config.get(getName(), "glassDropChance", glassDropChance,
				"Chance of dropping glass blocks, 1.0=100%, 0.0=0%").getDouble(glassDropChance);
	}

	public static int onGlassBreak(Random rand) {
		return rand.nextDouble() < glassDropChance ? 1 : 0;
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		try {
			if(ObfuscationDecoder.checkBoth("net.minecraft.block.BlockGlass", name)) {
				trans.startTransform();
				MethodNode method = trans.findMethod("quantityDropped", Type.getMethodDescriptor(Type.INT_TYPE, Type.getType(Random.class)));
				InsnList insn = new InsnList();
				insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				Method reflMethod = ModuleStrongGlass.class.getDeclaredMethod("onGlassBreak", Random.class);
				insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ModuleStrongGlass.class.getCanonicalName().replaceAll("\\.", "/"),
						reflMethod.getName(), Type.getMethodDescriptor(reflMethod)));
				insn.add(new InsnNode(Opcodes.IRETURN));
				method.instructions = insn;
				trans.stopTransform();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
