package tco.modulartweaks.module.mjirc;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;

import java.util.Arrays;
import java.util.EnumSet;

import net.minecraftforge.common.Property;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tco.modulartweaks.ModularTweaks;
import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;
import tco.modulartweaks.module.ModuleImpl;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ModuleMJIrc extends ModuleImpl implements ITickHandler {
	/*
	PASS <password>
	NICK <nickname>
	USER <username> <hostname> <servername> <realname>
	CTCP ?
	 */
	public static String server = "irc.esper.net";
	public static int port = 6667;

	private static boolean enabled = false;
	private static ModuleMJIrc instance;

	public static boolean handleClientCommand(String chat) {
		if(!enabled) return false;
		return instance.handleClientCommandInternal(chat);
	}

	private MJIrcChatManager manager;

	//return true captures the chat message
	private boolean handleClientCommandInternal(String chat) {
		if(chat.startsWith(manager.getCommandName())) {
			String[] args = chat.split(" ");
			args = Arrays.copyOfRange(args, 1, args.length);
			manager.handleCommand(args);
			return true;
		}
		return manager.handleChat(chat);
	}

	/*
	 * IModule
	 */

	@Override
	public void initialize() {
		enabled = true;
		instance = this;
		manager = new MJIrcChatManager("irc");
		ModularTweaks.logger.info(String.format("<<MJIRC>> %s %s %s", enabled, instance, manager));
		TickRegistry.registerTickHandler(this, Side.CLIENT);
	}

	@Override
	public Property[] getConfig() {
		return NONE;
	}

	@Override
	public String getDescription() {
		return "IRC client for hardcore users";
	}

	@Override
	public String getName() {
		return "MJIrc";
	}

	@Override
	public boolean setConfig(String key, String value) {
		return false;
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		if(ObfuscationDecoder.checkBoth("net.minecraft.client.Minecraft", name)) {
			trans.startTransform();
			MethodNode method = trans.findMethod("handleClientCommand", "(Ljava/lang/String;)Z");
			InsnList insert = new InsnList();
			insert.add(new VarInsnNode(ALOAD, 1));
			insert.add(new MethodInsnNode(INVOKESTATIC, getClass().getCanonicalName().replace('.', '/'), "handleClientCommand", "(Ljava/lang/String;)Z"));
			insert.add(new InsnNode(IRETURN));
			method.instructions.insert(insert);
			trans.stopTransform();
		}
	}

	/*
	 * ITickHandler
	 */

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		manager.update();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return getClass().getSimpleName();
	}

}
