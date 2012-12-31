package tco.modulartweaks.module.mjirc;

import java.io.BufferedWriter;
import java.io.IOException;

import net.minecraft.client.gui.GuiNewChat;
import cpw.mods.fml.client.FMLClientHandler;

public class MJIrcChatManager {
	public static final String ENDL = "\r\n";

	private static enum Mode {
		DEFAULT("Default"), DIRECT("Direct"), PREFIX("Prefix");

		public static final Mode[] MODES = { DEFAULT, DIRECT, PREFIX };

		private final String name;

		Mode(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	private MJIrcConnection connection;
	private String name;
	private Mode mode = Mode.DEFAULT;
	private String prefix = "";
	private boolean autoPong = false;

	public MJIrcChatManager(String name) {
		connection = new MJIrcConnection("", 0); //ninja connection
		this.name = "/" + name;
	}

	public String getCommandName() {
		return name;
	}

	// command
	public void handleCommand(String[] args) {
		if (args.length > 0) {
			if ("connect".equalsIgnoreCase(args[0])) {
				String defaultServer = ModuleMJIrc.server;
				int defaultPort = ModuleMJIrc.port;
				if(args.length > 2) {
					defaultServer = args[1];
				}
				if(args.length > 3) {
					try {
						defaultPort = Integer.parseInt(args[2]);
					} catch(Exception e) {}
				}
				if(connection != null) connection.disconnect();
				final String server = defaultServer;
				final int port = defaultPort;
				printChatMessage("[Irc] Connnecting to: " + defaultServer + ":" + defaultPort);
				connection = new MJIrcConnection(server, port);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							connection.connect();
						} catch (IOException e) {
							printChatMessage("[Irc] Connection error: " + e.getMessage());
						}
					}
				}).start();
			} else if ("disconnect".equalsIgnoreCase(args[0])) {
				connection.disconnect();
				printChatMessage("[Irc] Disconnected");
			}
			else if ("mode".equalsIgnoreCase(args[0])) {
				if (args.length == 1) {
					mode = Mode.MODES[(mode.ordinal() + 1) % Mode.MODES.length];
				} else {
					for (Mode refMode : Mode.MODES) {
						if (refMode.name().equalsIgnoreCase(args[1])) {
							mode = refMode;
						}
					}
				}
				printChatMessage("[Irc] Mode set to: " + mode);
			} else if ("prefix".equalsIgnoreCase(args[0])) {
				if (args.length == 1)
					prefix = "";
				else {
					StringBuffer sb = new StringBuffer();
					for (int i = 1; i < args.length - 1; i++) {
						sb.append(args[i]).append(' ');
					}
					sb.append(args[args.length - 1]);
					prefix = sb.toString();
					printChatMessage("[Irc] Prefix set to: " + prefix);
				}
			} else if ("pong".equalsIgnoreCase(args[0])) {
				autoPong = !autoPong;
				printChatMessage("[Irc] Auto PONG " + (autoPong ? "enabled" : "disabled"));
			}
		}
	}

	// chat
	public boolean handleChat(String chat) {
		if ((mode == Mode.PREFIX || mode == Mode.DIRECT) && connection.open()) {
			if(mode == Mode.PREFIX) {
				chat = prefix + chat;
			}
			printChatMessage("> " + chat);
			sendMessage(chat);
			return true;
		}
		return false;
	}

	private GuiNewChat guiChat;

	public void printChatMessage(String message) {
		if (guiChat == null) {
			guiChat = FMLClientHandler.instance().getClient().ingameGUI
					.getChatGUI();
		}
		guiChat.printChatMessage(message);
	}

	// client tick
	public void update() {
		String line = connection.read();
		if (line != null) {
			if(autoPong && line.startsWith("PING ")) {
				String pong = "PONG " + line.substring(5);
				sendMessage(pong);
				printChatMessage("[Irc] Sent: " + pong);
			}
			printChatMessage(line);
		}
	}

	public void sendMessage(String text) {
		BufferedWriter writer = connection.write();
		try {
			writer.write(text);
			writer.write(ENDL);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
