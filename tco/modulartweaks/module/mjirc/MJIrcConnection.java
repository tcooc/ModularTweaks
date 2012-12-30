package tco.modulartweaks.module.mjirc;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MJIrcConnection {

	private final String server;
	private final int port;
	private final Queue<String> messageQueue;

	private boolean open = false;
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private Thread listener;

	public MJIrcConnection(String server, int port) {
		messageQueue = new ConcurrentLinkedQueue<String>();
		this.server = server;
		this.port = port;
	}

	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(server, port);
		writer = new BufferedWriter(new OutputStreamWriter(	socket.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		listener = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String line;
					while(open && (line = reader.readLine()) != null) {
						messageQueue.add(line);
					}
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		listener.start();
		open = true;
	}

	public void disconnect() {
		try {
			if(socket != null) {
				writer.close();
				reader.close();
				socket.close();
			}
		} catch(Exception e) {
		} finally {
			socket = null;
			open = false;
		}
	}

	public String read() {
		return messageQueue.poll();
	}

	public BufferedWriter write() {
		return writer;
	}

	public boolean open() {
		return open;
	}

}
