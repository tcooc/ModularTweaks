package tco.modulartweaks.module;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.IRETURN;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;

public class ModuleEntityLimit extends ModuleImpl {

	public static boolean disableItem = false;
	public static boolean disableExp = false;

	@Override
	public Property[] getConfig() {
		Property[] config = new Property[2];
		config[0] = new Property("disableItem", String.valueOf(disableItem), Type.BOOLEAN);
		config[0].comment = "Disables item drops";
		config[1] = new Property("disableExp", String.valueOf(disableExp), Type.BOOLEAN);
		config[1].comment = "Disables experience drops";
		return config;
	}

	@Override
	public String getDescription() {
		return "Stops item or exp spawns";
	}

	@Override
	public String getName() {
		return "EntityLimit";
	}

	@Override
	public boolean setConfig(String key, String value) {
		if("disableItem".equals(key)) {
			disableItem = Boolean.valueOf(value);
			return true;
		}
		if("disableExp".equals(key)) {
			disableExp = Boolean.valueOf(value);
			return true;
		}
		return false;
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		if(ObfuscationDecoder.checkBoth("net.minecraft.world.World", name)) {
			trans.startTransform();
			String signature = "(L" +
					ObfuscationDecoder.getCorrectDesc("net.minecraft.entity.Entity") + ";)Z";
			MethodNode method = trans.findMethod("spawnEntityInWorld", signature);
			InsnList insert = new InsnList();
			LabelNode l1 = new LabelNode();
			LabelNode l2 = new LabelNode();
			insert.add(new VarInsnNode(ALOAD, 1));
			insert.add(new TypeInsnNode(INSTANCEOF, ObfuscationDecoder.getCorrectDesc("net.minecraft.entity.item.EntityItem")));
			insert.add(new JumpInsnNode(IFEQ, l1));
			insert.add(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "disableItem", "Z"));
			insert.add(new JumpInsnNode(IFEQ, l1));
			insert.add(new InsnNode(ICONST_0));
			insert.add(new InsnNode(IRETURN));
			insert.add(l1);
			insert.add(new VarInsnNode(ALOAD, 1));
			insert.add(new TypeInsnNode(INSTANCEOF, ObfuscationDecoder.getCorrectDesc("net.minecraft.entity.item.EntityXPOrb")));
			insert.add(new JumpInsnNode(IFEQ, l2));
			insert.add(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "disableExp", "Z"));
			insert.add(new JumpInsnNode(IFEQ, l2));
			insert.add(new InsnNode(ICONST_0));
			insert.add(new InsnNode(IRETURN));
			insert.add(l2);
			method.instructions.insert(insert);
			trans.stopTransform();
		}
	}
}