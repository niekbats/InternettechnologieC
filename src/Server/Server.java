package Server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import model.Gebruiker;

public class Server {
	// HTTP poort
	final static int SERVER_PORT = 80;
	// Wat arrylists
	private static ArrayList<Socket> verbonden = new ArrayList<Socket>();
	private static ArrayList<String> beveiligdePaden = new ArrayList<String>();
	private static ArrayList<Gebruiker> gebruikers = new ArrayList<Gebruiker>();
	
	@SuppressWarnings("resource")
	Server(ArrayList<Gebruiker> gebruikers) {
		// leest htacces uit
		File htacces = new File(".htacces");
		try {
			Scanner htaccesScanner = new Scanner(htacces);
			while (htaccesScanner.hasNextLine()) {
				beveiligdePaden.add(htaccesScanner.nextLine());
			}
		
		} catch (FileNotFoundException e1) {
			System.out.println(".htacces is niet gevonden");
			e1.printStackTrace();
		}

	

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
				ct.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public class ClientThread extends Thread {
		private InputStream inputStream;
		private OutputStream outputStream;

		private Socket socket;

		private ArrayList<Gebruiker> gebruikers;

		public void setGebruikers(ArrayList<Gebruiker> gebruikersLijst) {
			gebruikers = gebruikersLijst;
		}

		public void setBeveiligdePaden(ArrayList<String> beveiligde) {
			beveiligdePaden = beveiligde;
		}

		public ClientThread(Socket socket) {
			this.socket = socket;
		}
// Run methode van de server, deze gaat door totdat de socket dichtis
		public void run() {
			String request = "";
			while (!socket.isClosed()) {
				try {
					inputStream = socket.getInputStream();
					outputStream = socket.getOutputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

					String s;
					s = reader.readLine();
					// blijft hier totdat er een keer geen null wordt ontvangen
					if (s != null) {
						request = s;
						System.out.println("RESQUEST =" + request);
					} else {
						break;
					}

					// zolang het bericht niet beeindigt is word s
					// reader.readline
					String header = "";
					while ((s = reader.readLine()) != null) {
						System.out.println(s);
						header = header + "\n" + s;
						if (s.isEmpty()) {
							break;
						}
					}
// Kijk of de request iets bevat bijvoorbeld index.html dan weet de erver dat die de index html moet opgeven
					if (request.contains("index.html")) {
						sendHTML(socket);
					} else if (request.contains("tijger.jpeg")) {
						sendPicture(socket);
					} else if (request.contains("style.ccs")) {
						sendCCS(socket);
					} else if (request.contains("halloworld.js")) {
						sendJS(socket);
					} else if (request.contains("beveiligd.html")) {
						if (beveiligdePaden.contains("beveiligd.html")) {
							System.out.println("de gestuurde request: ");
							System.out.println(request);
							System.out.println();

							System.out.println("de header is: ");
							System.out.println(header);
							System.out.println();

							sendBeveiligd(socket, header);
						}else if (request.contains("/")){
							
							int aantal =0;
							for(int i =0 ; i < request.length() ; i++){
								
								if(request.substring(i, i+1).contains("/")){
									aantal++;
								}
							}
							if(aantal ==1){
								sendRoot(socket);
							}
						}
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
/**
 * @author Ernst
 * @param socket
 * Verzend een plaatje
 */
		public void sendPicture(Socket socket) {

			PrintWriter out = new PrintWriter(outputStream);
			out.write("HTTP/1.1 200 OK\r\n");
			out.write("Date: Mon, 24 Okt 2015 13:00:00 GMT\r\n");
			out.write("Server: Apache/0.8.4\r\n");
			out.write("Content-Type: image/jpeg\r\n");

			File file = new File("tijger.jpg");
			try (InputStream is = new BufferedInputStream(new FileInputStream(file));
			        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());) {
			    dos.writeLong(file.length()); // <-- remember to read a long on server.
			    int val;
			    while ((val = is.read()) != -1) {
			        dos.write(val);
			    }
			 
			

			out.write("Content-Length:" + file.length() + "\r\n");
			out.write("Connection: close\r\n");
			out.write("\r\n");
			
			System.out.println("Plaatje wordt verstuurd! ");
			dos.flush();
			out.flush();
			
			} catch ( IOException e) {
				System.out.println("Er ging iets verkeerd!");
			}
		}
/**
 * @author Ernst
 * @param socket De socket waar die mee verbonden is
 * 
 * Deze methode zend een html pagina , deze wordt gelezen door BufferedReader en verwerkt door de String builder
 */
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
/**
 * @author Ernst
 * @param socket 
 * Zend een CCS bestand
 */
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
/**
 * @author Ernst
 * @param socket
 * Zend een JS
 */
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
/**
 * @author Ernst
 * @param socket
 * Zend the root
 */
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
/**
 * @author Ernst
 * @param socket
 */
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
/**
 * @author Niek
 * @param socket
 * @param header
 */
		public void sendBeveiligd(Socket socket, String header) {

			// zonder authenticatie meesturen
			if (!header.contains("Authorization: Basic ")) {
				sendMissingAuthentication();
				return;
			}

			String[] splitHeader = header.split("\n");

			for (int i = 0; i < splitHeader.length; i++) {
				if (splitHeader[i].contains("Authorization: Basic ")) {
					String encryptedInloggegevens = splitHeader[i].replaceFirst("Authorization: Basic ", "");

					// decryption magic
					String decryptedInloggegevens = new String(Base64.getDecoder().decode(encryptedInloggegevens));
					String[] naamWachtwoord = decryptedInloggegevens.split(":");
					String naam = naamWachtwoord[0];
					String wachtwoord = naamWachtwoord[1];

					System.out.println(decryptedInloggegevens);

					// loopen door alle gebruikers om een matchend naam
					// wachtwoord paar te vinden
					for (Gebruiker g : gebruikers) {
						if (g.getGebruikersNaam().equals(naam)) {
							if (g.getWachtwoord().equals(wachtwoord)) {
								// juiste authenticatie meesturen
								PrintWriter out = new PrintWriter(outputStream);
								out.write("HTTP/1.1 200 OK\r\n");
								out.write("Date: Mon, 24 Okt 2015 13:00:00 GMT\r\n");
								out.write("Server: Apache/0.8.4\r\n");
								out.write("Content-Type: text/html\r\n");

								StringBuilder contentBuilder = new StringBuilder();
								try {
									BufferedReader in = new BufferedReader(new FileReader("beveiligd.html"));
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
							} else {
								// correcte naam maar fout wachtwoord
								sendWrongAuthentication();
								return;
							}
						}
					}
				}
			}
			// foute authenticatie meesturen
			sendWrongAuthentication();

		}
/**
 * @author Niek
 */
		public void sendMissingAuthentication() {
			PrintWriter out = new PrintWriter(outputStream);
			out.write("HTTP/1.1 401 ACCES DENIED \r\n");
			out.write("Date: Mon, 25 Okt 2015 13:00:00 GMT\r\n");
			out.write("Server: Apache/0.8.4\r\n");
			String content = "Error 401 Geheeft geen toegang, log in!";
			out.write("Content-Length:" + content.length() + "\r\n");
			out.write("Connection: close\r\n");
			out.write("\r\n");
			out.write(content);
			out.flush();
		}
/**
 * @author Niek
 */
		public void sendWrongAuthentication() {
			PrintWriter out = new PrintWriter(outputStream);
			out.write("HTTP/1.1 401 ACCES DENIED \r\n");
			out.write("Date: Mon, 25 Okt 2015 13:00:00 GMT\r\n");
			out.write("Server: Apache/0.8.4\r\n");
			out.write("Content-Type: text/html\r\n");
			String content = "Error 401 Bad authentication gebruikersnaam of wachtwoord foutief ingevoerd";
			out.write("Content-Length:" + content.length() + "\r\n");
			out.write("Connection: close\r\n");
			out.write("\r\n");
			out.write(content);
			out.flush();
		}
	}
}
