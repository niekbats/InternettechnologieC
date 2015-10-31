package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	final static int SERVER_PORT = 80;

	private static ArrayList<Socket> verbonden = new ArrayList<Socket>();
	
	private static ArrayList<String> beveiligdePaden = new ArrayList<String>();
	
	@SuppressWarnings("resource")
	Server() {
		//leest htacces uit
		File htacces = new File(".htacces");
		try {
			Scanner htaccesScanner = new Scanner(htacces);
			while(htaccesScanner.hasNextLine()) {
				beveiligdePaden.add(htaccesScanner.nextLine());
			}
			System.out.println(beveiligdePaden);
		} catch (FileNotFoundException e1) {
			System.out.println(".htacces is niet gevonden");
			e1.printStackTrace();
		}
		
		ClientThread.setBeveiligdePaden(beveiligdePaden);
		
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			System.out.println("Socket aangemaakt");
		} catch (IOException e) {
			System.out.println("de server poort: " + SERVER_PORT + " is bezet");
			e.printStackTrace();
		}

		// Wacht op binnenkomende client connectie requests.
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				verbonden.add(socket);
				System.out.println(verbonden.size());
				// Als er een verbinding tot stand is gebracht, start een nieuwe
				// ClientThread.
				ClientThread ct = new ClientThread(socket);
				System.out.println("Verbinding tot stand gebracht met client!");
				ct.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}