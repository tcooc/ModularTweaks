package tco.modulartweaks;

import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import tco.modulartweaks.module.IModule;
import cpw.mods.fml.relauncher.IClassTransformer;

public class ModularTweaksTransformer implements IClassTransformer {

	private byte[] bytecode;
	private ClassReader classReader;
	private ClassWriter classWriter;
	private ClassNode classNode;

	public ClassNode getClassNode() {
		return classNode;
	}

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
		bytecode = bytes;
		classWriter = null;
		try {
			for(IModule module : ModularTweaks.instance.clientModules) {
				module.transform(this, name);
			}
			for(IModule module : ModularTweaks.instance.commonModules) {
				module.transform(this, name);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(classWriter != null) {
			return classWriter.toByteArray();
		}
		return bytes;
	}

	public void startTransform() {
		classNode = new ClassNode();
		classReader = new ClassReader(bytecode);
		classReader.accept(classNode, 0);
	}

	public void stopTransform() {
		classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(classWriter);
	}

	public MethodNode findMethod(String name, String desc) {
		for(MethodNode method : (List<MethodNode>) classNode.methods) {
			if(ObfuscationDecoder.checkBoth(name, method.name) && desc.equals(method.desc)) {
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
