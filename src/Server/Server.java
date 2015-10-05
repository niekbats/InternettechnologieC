package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	final static int SERVER_PORT = 80;
	private InputStream inputStream;
	private OutputStream outputStream;

	private static ArrayList<Socket> verbonden = new ArrayList<Socket>();

	@SuppressWarnings("resource")
	Server() {

		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			System.out.println("Socket aangemaakt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Wacht op binnenkomende client connectie requests.
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				verbonden.add(socket);
				System.out.println(verbonden.size());
				// Als er een verbinding tot stand is gebracht, start een nieuwe
				// thread.
				ClientThread ct = new ClientThread(socket);
				System.out.println("Verbinding tot stand gebracht met client!");
				ct.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public class ClientThread extends Thread {
		private Socket socket;

		public ClientThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {

			while (socket.isConnected()) {

				try {

					// for(int i = 0; i<verbonden.size() ;i++){
					inputStream = socket.getInputStream();
					outputStream = socket.getOutputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

					String line = reader.readLine();

					if (line == null) {
						break;
					}

					while (!reader.ready()) {
						line = reader.readLine();
						if (line == null) {
							break;
						}
					}
					PrintWriter out = new PrintWriter(outputStream);
					out.write("HTTP/1.1 200 OK\r\n");
					out.write("Date: Mon, 05 Okt 2015 16:56:00 GMT\r\n");
					out.write("Server: Apache/0.8.4\r\n");
					out.write("Content-Type: text/html\r\n");
					// out.write("Content-Length: 138\r\n");
					// out.write("Connection: close\r\n");
					out.write("\r\n");

					out.write("<!doctype html\">\n" + "<html>\n" + "<head><title>Voorbeeld</title></head>\n"
							+ "<body>\n" + "Dit is een voorbeeld" + "</body></html>\r\n");
					System.out.println("Data die wordt verstuurd: ");
					out.flush();

					// }

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
}
