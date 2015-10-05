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
		// Maak een socket aan om op clients te wachten
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			System.out.println("Socket aangemaakt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Wacht op binnenkomende client connectie requests.
while(true){
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

		// start de thread en roept run() aan. Gebruik hier niet
		// run(): dan wordt de code in de huidige thread gedraaid.
	}}

	public class ClientThread extends Thread {
		private Socket socket;
		
		public ClientThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
		
			
			
			while (true) {
				
				try {
					
					
					for(int i = 0; i<verbonden.size() ;i++){
						inputStream = socket.getInputStream();
						outputStream = verbonden.get(i).getOutputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
						String line = reader.readLine();
						if(line==null){
							break;
						}
						
						PrintWriter writer = new PrintWriter(outputStream);
						writer.println("<!doctype html\">\n" + "<html>\n" + "<head><title>Verhuurder</title></head>\n" + "<body>\n"
								+ "<h1>Verhuurd: </h1>\n"
								+ "<table style='100%'>\n<tr>\n<td>Naam</td>\n<td>Vierkante Meter</td>\n<td>HuurPrijs</td>\n<td>Plaats</td></tr>");
						System.out.println("Data die wordt verstuurd: "+ line);
						writer.flush();// vetelt het systeem om alle uistaande data te
										// versturen
					}
		
					
//					PrintWriter writer = new PrintWriter(outputStream);
//					writer.println(line);
//					writer.flush();// vetelt het systeem om alle uistaande data te
//									// versturen

					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 1. Wacht op berichten van de client.
				// 2. Stuur berichten van de clients door naar de andere
				// clients. (Broadcast)
							}
		}
	}
}
