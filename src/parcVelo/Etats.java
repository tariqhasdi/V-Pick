/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parcVelo;

/**
 *
 * @author Tariq
 */

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