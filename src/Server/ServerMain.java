package Server;

import java.util.ArrayList;

import model.Gebruiker;

public class ServerMain {

	public static void main(String[] args) {
		// maakt gebruikers aan en geeft ze door aan de clientthreads
		ArrayList<Gebruiker> gebruikers = new ArrayList<Gebruiker>();
		gebruikers.add(new Gebruiker("Henk", "Henkie"));
		gebruikers.add(new Gebruiker("Tester", "test"));

		// start de server
		Server server = new Server(gebruikers);
	}
}
