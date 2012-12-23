package tco.modulartweaks;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.IClassTransformer;

public class ModularTweaksTransformer implements IClassTransformer {

	public ModularTweaksTransformer() {
		//load configs here...
	}

	private ClassReader classReader;
	private ClassWriter classWriter;
	private ClassNode classNode;

	//iconst_0	03 : 0
	//iconst_1	04 : 1
	//iload	    15 : load integer onto stack(1)	
	//aload     19 : load reference onto stack from local var (1)
	//ireturn	ac : return
	//return	b1 : return
	//getstatic	b2 : static reference (2)
	//invokestatic	b8	:
	//instanceof	c1 : instanceof (2)
	//ifeq	99     : if== (2)
	@Override
	public byte[] transform(String name, byte[] bytes) {
		if(name.startsWith("tco")) return bytes; //idk why this happens :/
		classWriter = null;
		try {
			if(ObfuscationHelper.checkBoth("net.minecraft.block.BlockGlass", name)) {
				ModularTweaks.logger.info("Injecting BlockGlass edits");
				//Strong Glass
				startTransform(bytes);
				MethodNode method = findMethod(classNode, "quantityDropped", Type.getMethodDescriptor(Type.INT_TYPE, Type.getType(Random.class)));
				if(method != null) {
					InsnList insn = new InsnList();
					insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
					Method reflMethod = ModuleStrongGlass.class.getDeclaredMethod("onGlassBreak", Random.class);
					insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ModuleStrongGlass.class.getCanonicalName().replaceAll("\\.", "/"),
							reflMethod.getName(), Type.getMethodDescriptor(reflMethod)));
					insn.add(new InsnNode(Opcodes.IRETURN));
					method.instructions = insn;
				}
				stopTransform();
			} else if(ObfuscationHelper.checkBoth("net.minecraft.block.BlockCactus", name)) {
				ModularTweaks.logger.info("Injecting BlockCactus changes.");
				//Cactus Proof
				startTransform(bytes);
				MethodNode method = findMethod(classNode, "onEntityCollidedWithBlock", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(World.class), Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE, Type.getType(Entity.class)));
				if(method != null) {
					printMethod(method);
					InsnList insn = method.instructions;
					InsnList insert = new InsnList();
					LabelNode label = new LabelNode();
					insert.add(new VarInsnNode(Opcodes.ALOAD, 5));
					insert.add(new TypeInsnNode(Opcodes.INSTANCEOF, EntityItem.class.getCanonicalName().replaceAll("\\.", "/")));
					//insert.add(new VarInsnNode(Opcodes.ALOAD, 5));
					insert.add(new JumpInsnNode(Opcodes.IFEQ, label));
					insert.add(new InsnNode(Opcodes.RETURN));
					insert.add(label);
					//insert.add(new FrameNode());
					//insert.add(new VarInsnNode(Opcodes.ALOAD, 5));
					insn.insert(insert);
				}
				stopTransform();
			} else if(ObfuscationHelper.checkBoth("net.minecraft.block.BlockLog", name)) {
				ModularTweaks.logger.info("BlockLog");
				//Tree Gravity
				startTransform(bytes);
				MethodNode method = findMethod(classNode, "breakBlock", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(World.class), Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE));
				if(method != null) {
					InsnList insn = new InsnList();
					insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
					insn.add(new VarInsnNode(Opcodes.ILOAD, 2));
					insn.add(new VarInsnNode(Opcodes.ILOAD, 3));
					insn.add(new VarInsnNode(Opcodes.ILOAD, 4));
					insn.add(new VarInsnNode(Opcodes.ILOAD, 5));
					insn.add(new VarInsnNode(Opcodes.ILOAD, 6));
					Method reflMethod = ModuleTreeGravity.class.getDeclaredMethod("onWoodBreak", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
					insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ModuleTreeGravity.class.getCanonicalName().replaceAll("\\.", "/"),
							reflMethod.getName(), Type.getMethodDescriptor(reflMethod)));
					insn.add(new InsnNode(Opcodes.RETURN));
					method.instructions.insert(insn);
				}
				stopTransform(); //*/
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(classWriter != null) {
			return classWriter.toByteArray();
		}
		return bytes;
	}

	private void startTransform(byte[] bytes) {
		classNode = new ClassNode();
		classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
	}

	private void stopTransform() {
		classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(classWriter);
	}

	private static MethodNode findMethod(ClassNode classNode, String name, String desc) {
		for(MethodNode method : (List<MethodNode>) classNode.methods) {
			if(ObfuscationHelper.checkBoth(name, method.name) && desc.equals(method.desc)) {
				return method;
			}
		}
		return null;
	}

	private void printMethod(MethodNode method) {
		ModularTweaks.logger.info("Printing " + method.name + " " + method.desc);
		for(int i = 0; i < method.instructions.size(); i++) {
			ModularTweaks.logger.info(method.instructions.get(i).getType() +
					" " + Integer.toHexString(method.instructions.get(i).getOpcode()));
		}
		ModularTweaks.logger.info("---------------------------------------------");
	}

}
