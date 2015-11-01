package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
			String request = "";
			while (!socket.isClosed()) {

				try {

					inputStream = socket.getInputStream();
					outputStream = socket.getOutputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

					String s;
					s = reader.readLine();
					if (s != null) {
						request = s;
						// request = s.substring(4,(s.length() -8));
					} else {
						break;
					}

					while ((s = reader.readLine()) != null) {
						System.out.println(s);
						if (s.isEmpty()) {
							break;
						}
					}

					if (request.contains("index.html")) {
						sendHTML(socket);
					} else if (request.contains("tijger.jpeg")) {
						sendPicture(socket);
					} else if (request.contains("style.ccs")) {
						sendCCS(socket);
					} else if (request.contains("halloworld.js")) {
						sendJS(socket);
					} else {
						notFound(socket);
					}
					socket.close();
				}

				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		public void sendPicture(Socket socket) {

			PrintWriter out = new PrintWriter(outputStream);
			out.write("HTTP/1.1 200 OK\r\n");
			out.write("Date: Thu, 29 Okt 2015 13:00:00 GMT\r\n");
			out.write("Server: Apache/0.8.4\r\n");
			out.write("Content-Type: image/jpeg\r\n");
			
			File file = new File("tijger.jpeg");
			DataOutputStream dos = null;
			try {
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Datainputstream");
			}
			

			FileInputStream fos = null;
			try {
				fos = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				System.out.println("FileoutputSTream" + file + " <-- text");
			}
			int arrylength = dos.size();
			byte[] b = new byte[arrylength];
			try {
				dos.write(b);
			} catch (IOException e) {
				System.out.println("Read dis.b");
			}

			out.write("Content-Length:" + b.length + "\r\n");
			out.write("Connection: close\r\n");
			out.write("\r\n");
			
			System.out.println("Plaatje wordt verstuurd! ");


			// finaly
			out.flush();

		}

		public void sendHTML(Socket socket) {
			PrintWriter out = new PrintWriter(outputStream);
			out.write("HTTP/1.1 200 OK\r\n");
			out.write("Date: Mon, 24 Okt 2015 13:00:00 GMT\r\n");
			out.write("Server: Apache/0.8.4\r\n");
			out.write("Content-Type: text/html\r\n");

			StringBuilder contentBuilder = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(new FileReader("index.html"));
				String str;
				while ((str = in.readLine()) != null) {
					contentBuilder.append(str);
				}
				in.close();
			} catch (IOException e) {
			}
			String content = contentBuilder.toString();

			out.write("Content-Length:" + content.length() + "\r\n");
			out.write("Connection: close\r\n");
			out.write("\r\n");
			out.write(content);
			System.out.println("Data die wordt verstuurd! ");
			out.flush();
		}

		public void sendCCS(Socket socket) {
			PrintWriter out = new PrintWriter(outputStream);
			out.write("HTTP/1.1 200 OK\r\n");
			out.write("Date: Mon, 24 Okt 2015 13:00:00 GMT\r\n");
			out.write("Server: Apache/0.8.4\r\n");
			out.write("Content-Type: text/html\r\n");

			StringBuilder contentBuilder = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(new FileReader("style.ccs"));
				String str;
				while ((str = in.readLine()) != null) {
					contentBuilder.append(str);
				}
				in.close();
			} catch (IOException e) {
			}
			String content = contentBuilder.toString();

			out.write("Content-Length:" + content.length() + "\r\n");
			out.write("Connection: close\r\n");
			out.write("\r\n");
			out.write(content);
			System.out.println("CCS wordt verstuurd! ");
			out.flush();
		}

		public void sendJS(Socket socket) {
			PrintWriter out = new PrintWriter(outputStream);
			out.write("HTTP/1.1 200 OK\r\n");
			out.write("Date: Mon, 24 Okt 2015 13:00:00 GMT\r\n");
			out.write("Server: Apache/0.8.4\r\n");
			out.write("Content-Type: text/html\r\n");

			StringBuilder contentBuilder = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(new FileReader("hw.js"));
				String str;
				while ((str = in.readLine()) != null) {
					contentBuilder.append(str);
				}
				in.close();
			} catch (IOException e) {
			}
			String content = contentBuilder.toString();

			out.write("Content-Length:" + content.length() + "\r\n");
			out.write("Connection: close\r\n");
			out.write("\r\n");
			out.write(content);
			System.out.println("JS wordt verstuurd! ");
			out.flush();
		}

		public void sendRoot(Socket socket) {
			PrintWriter out = new PrintWriter(outputStream);
			out.write("HTTP/1.1 200 OK\r\n");
			out.write("Date: Mon, 24 Okt 2015 13:00:00 GMT\r\n");
			out.write("Server: Apache/0.8.4\r\n");
			out.write("Content-Type: text/html\r\n");

			StringBuilder contentBuilder = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(new FileReader("root.html"));
				String str;
				while ((str = in.readLine()) != null) {
					contentBuilder.append(str);
				}
				in.close();
			} catch (IOException e) {
			}
			String content = contentBuilder.toString();

			out.write("Content-Length:" + content.length() + "\r\n");
			out.write("Connection: close\r\n");
			out.write("\r\n");
			out.write(content);
			System.out.println("ROOT wordt verstuurd! ");
			out.flush();
		}

		public void notFound(Socket socket) {
			PrintWriter out = new PrintWriter(outputStream);
			out.write("HTTP/1.1 404 Not Found\r\n");
			out.write("Date: Mon, 25 Okt 2015 13:00:00 GMT\r\n");
			out.write("Server: Apache/0.8.4\r\n");
			out.write("Content-Type: text/html\r\n");
			String content = "Error 404 pagina niet gevonden";
			out.write("Content-Length:" + content.length() + "\r\n");
			out.write("Connection: close\r\n");
			out.write("\r\n");
			out.write(content);
			out.flush();
		}
	}
}
