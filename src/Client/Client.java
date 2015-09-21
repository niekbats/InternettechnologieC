package Client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	final static int SERVER_PORT = 801;
	final static String SERVER_ADDRESS = "localhost";
	private InputStream inputStream;
	private OutputStream outputStream;
	private String clientID = "test";
	private Socket socket = null;

	Client() {
		// Maak een socket voor de verbinding met de server
		Scanner invoer = new Scanner(System.in);
		System.out.println("Naam van de client ?");
		clientID = invoer.nextLine();
		// invoer.close();

		try {
			socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			System.out.println("Verbonden!");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Creëer een input en output stream
		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// try {
		// OutputStream outputStream = new
		// BufferedOutputStream(socket.getOutputStream());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Creëer een thread om berichten van de server te ontvangen
		ServerSender serverListener = new ServerSender();
		serverListener.run();

	}

	public class ServerSender extends Thread {

		public void run() {
			while (true) {
				Scanner in = new Scanner(System.in);
				System.out.println("Kies verzenden of ontvangen als optie");
				String invoer = in.nextLine();

				if (invoer.equals("verzenden")) {
					PrintWriter writer = new PrintWriter(outputStream);
					System.out.println("Voer een bericht in");
					String tekst = in.nextLine();
					writer.println("[" + clientID + "] " + tekst);
					writer.flush();// vetelt het systeem om alle uistaande data
									// te
									// versturen
				} else if (invoer.equals("ontvangen")) {

					try {
						inputStream = socket.getInputStream();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					try {
							String line = reader.readLine();
							System.out.println(line);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					System.out.println("Geef geldige optie: verzenden of ontvangen!");
				}
				// System.out.println(line);

			}

		}
	}
	
	public class ServerListener extends Thread {
		
		public void run() {
			while(true) {
				try {
					System.out.println("De inputstream wordt opgehaald");
					inputStream = socket.getInputStream();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				System.out.println("BufferdReader wordt gelezen");
				try {
						String line = reader.readLine();
						System.out.println(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
