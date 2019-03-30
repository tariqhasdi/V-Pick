package parcvelo;

public enum Etats {
	
	HS("HS"),
	OK("OK");

	private String etat;
	
	private Etats(String etat) {
		// TODO Auto-generated constructor stub
		this.etat = etat;
	}
	
	public String getEtat() {
		return etat;
	}



	
}
