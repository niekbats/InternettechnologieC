package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import model.Gebruiker;

public class ClientThread extends Thread {
	private InputStream inputStream;
	private OutputStream outputStream;
	
	private Socket socket;
	
	private static ArrayList<Gebruiker> gebruikers;
	private static ArrayList<String> beveiligdePaden;
	
	public static void setGebruikers(ArrayList<Gebruiker> gebruikersLijst) {
		gebruikers = gebruikersLijst;
	}
	
	public static void setBeveiligdePaden(ArrayList<String> beveiligde) {
		beveiligdePaden = beveiligde;
	}
	
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
				//blijft hier totdat er een keer geen null wordt ontvangen
				if (s != null) {
					request = s;
					// request = s.substring(4,(s.length() -8));
				} else {
					break;
				}
				
				//zolang het bericht niet beeindigt is word s reader.readline
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
				} else if (request.contains("beveiligd.html")) {
					if(beveiligdePaden.contains("beveiligd.html")) {
						sendBeveiligd(socket);
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

	public void sendPicture(Socket socket) {

		PrintWriter out = new PrintWriter(outputStream);
		out.write("HTTP/1.1 200 OK\r\n");
		out.write("Date: Mon, 24 Okt 2015 13:00:00 GMT\r\n");
		out.write("Server: Apache/0.8.4\r\n");
		out.write("Content-Type: image/jpeg\r\n");

		DataInputStream dis = null;
		try {
			dis = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Datainputstream");
		}
		File file = new File("tijger.jpeg");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("FileoutputSTream" + file + " <-- text");
		}
		int arrylength = 0;
		try {
			arrylength = dis.readInt();
		} catch (IOException e) {
			System.out.println("ReadInt");
		}
		byte[] b = new byte[arrylength];
		try {
			dis.readFully(b);
		} catch (IOException e) {
			System.out.println("Read dis.b");
		}

		out.write("Content-Length:" + b.length + "\r\n");
		out.write("Connection: close\r\n");
		out.write("\r\n");
		try {
			fos.write(b, 0, b.length);
		} catch (IOException e) {
			System.out.println("fos.write niet gelukt");
		}
		System.out.println("Plaatje wordt verstuurd! ");
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

	public void sendBeveiligd(Socket socket) {
		
		//zonder authenticatie meesturen
		sendMissingAuthentication();
		
		//foute authenticatie meesturen
		sendWrongAuthentication();
		
		//juiste authenticatie meesturen
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
	}
	
	public void sendMissingAuthentication() {
		PrintWriter out = new PrintWriter(outputStream);
		out.write("HTTP/1.1 401 ACCES DENIED \r\n");
		out.write("Date: Mon, 25 Okt 2015 13:00:00 GMT\r\n");
		out.write("Server: Apache/0.8.4\r\n");
		out.write("Content-Length:" + 0 + "\r\n");
		out.write("Connection: close\r\n");
		out.write("\r\n");
		out.flush();
	}
	
	public void sendWrongAuthentication() {
		PrintWriter out = new PrintWriter(outputStream);
		out.write("HTTP/1.1 401 ACCES DENIED \r\n");
		out.write("Date: Mon, 25 Okt 2015 13:00:00 GMT\r\n");
		out.write("Server: Apache/0.8.4\r\n");
		out.write("Content-Type: text/html\r\n");
		String content = "Error 401 Bad authentication gebruikersnaam of wachtwoord foutief";
		out.write("Content-Length:" + content.length() + "\r\n");
		out.write("Connection: close\r\n");
		out.write("\r\n");
		out.write(content);
		out.flush();
	}
}