package tco.modulartweaks.module;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.Configuration;
import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;

public class ModuleCrafting implements IModule {

	public static boolean enabled = false;

	@Override
	public void initialize() {
		enabled = true;
	}

	@Override
	public String getName() {
		return "Crafting";
	}

	@Override
	public String getDescription() {
		return "3x3 crafting anywhere";
	}

	@Override
	public void loadConfigs(Configuration config) {
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		try {
			if(ObfuscationDecoder.checkBoth("net.minecraft.inventory.ContainerWorkbench", name)) {
				trans.startTransform();
				MethodNode method = trans.findMethod("canInteractWith", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(EntityPlayer.class)));
				//if enabled
				InsnList insert = new InsnList();
				LabelNode l1 = new LabelNode();
				insert.add(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replaceAll("\\.", "/"), "enabled", "Z"));
				insert.add(new JumpInsnNode(IFEQ, l1));
				insert.add(new InsnNode(ICONST_1));
				insert.add(new InsnNode(IRETURN));
				insert.add(l1);
				trans.stopTransform(); //*/
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
