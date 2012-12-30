package tco.modulartweaks.module;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.RETURN;

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

public class ModuleCactusProof extends ModuleImpl {

	public static boolean enabled = false;

	@Override
	public String getDescription() {
		return "Stops cactus from destroying items.";
	}

	@Override
	public String getName() {
		return "Cactus Proof";
	}

	@Override
	public void initialize() {
		enabled = true;
	}

	//TODO config
	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		if(ObfuscationDecoder.checkBoth("net.minecraft.block.BlockCactus", name)) {
			trans.startTransform();
			/* Insert:
			 * if(entity instanceof EntityItem && ModuleCactusProof.enabled) return;
			 */
			String signature = "(L" +
					ObfuscationDecoder.getCorrectDesc("net.minecraft.world.World") + ";IIIL" +
					ObfuscationDecoder.getCorrectDesc("net.minecraft.entity.Entity") + ";)V";
			MethodNode method = trans.findMethod("onEntityCollidedWithBlock", signature);
			InsnList insn = method.instructions;
			InsnList insert = new InsnList();
			LabelNode l1 = new LabelNode();
			insert.add(new VarInsnNode(ALOAD, 5));
			insert.add(new TypeInsnNode(INSTANCEOF, ObfuscationDecoder.getCorrectDesc("net.minecraft.entity.item.EntityItem")));
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
