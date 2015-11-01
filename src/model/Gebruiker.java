package model;

public class Gebruiker {
	private final String gebruikersNaam;
	private String wachtwoord;
	
	public Gebruiker(String gebruikersNaam, String wachtwoord) {
		this.gebruikersNaam = gebruikersNaam;
		this.wachtwoord = wachtwoord;
	}

	public String getGebruikersNaam() {
		return gebruikersNaam;
	}

	public String getWachtwoord() {
		return wachtwoord;
	}

	public void setWachtwoord(String wachtwoord) {
		this.wachtwoord = wachtwoord;
	}
}
