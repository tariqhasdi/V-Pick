package parcvelo;

public enum Classifications {
	
	VPLUS("Vplus"),
	VNUL("Vnul"),
	VMOINS("Vmoins");
	

	private String etat;
	
	private Classifications(String etat) {
		// TODO Auto-generated constructor stub
		this.etat = etat;
	}
	
	public String getEtat() {
		return etat;
	}
	
	public void setEtat(String etat) {
		this.etat = etat;
	}




}
