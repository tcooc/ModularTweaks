package tco.modulartweaks.module;

import org.objectweb.asm.Type;
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

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;

public class ModuleCactusProof implements IModule {

	public static boolean enabled = false;

	@Override
	public void initialize() {
		enabled = true;
	}

	@Override
	public String getName() {
		return "Cactus Proof";
	}

	@Override
	public String getDescription() {
		return "Stops cactus from destroying items.";
	}

	@Override
	public void loadConfigs(Configuration config) {
	}

	//TODO config
	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		if(ObfuscationDecoder.checkBoth("net.minecraft.block.BlockCactus", name)) {
			trans.startTransform();
			/* Insert:
			 * if(entity instanceof EntityItem && ModuleCactusProof.enabled) return;
			 */
			MethodNode method = trans.findMethod("onEntityCollidedWithBlock", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(World.class), Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE, Type.getType(Entity.class)));
			InsnList insn = method.instructions;
			InsnList insert = new InsnList();
			LabelNode l1 = new LabelNode();
			insert.add(new VarInsnNode(ALOAD, 5));
			insert.add(new TypeInsnNode(INSTANCEOF, EntityItem.class.getCanonicalName().replace('.', '/')));
			insert.add(new JumpInsnNode(IFEQ, l1));
			insert.add(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "enabled", "Z"));
			insert.add(new JumpInsnNode(IFEQ, l1));
			insert.add(new InsnNode(RETURN));
			insert.add(l1);
			//insert.add(new FrameNode());
			//insert.add(new VarInsnNode(Opcodes.ALOAD, 5));
			insn.insert(insert);
			trans.stopTransform();
		}
	}

}
