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

import java.sql.*;

public class LesLocations {
	
	private Connection conn;
	
	private int idVelo;
	private int idClient;
	private Date dateD;
	private Date dateF;
	private double cout;
	private String codeSecret;
	private int numBornetteD;
	private int numBornetteA;
	
	
	public LesLocations(Connection conn, int idClient) {
		// TODO Auto-generated constructor stub
	}
	public LesLocations(Connection conn, String codeSecret) {
		// TODO Auto-generated constructor stub
		this.codeSecret = codeSecret;
	}
	public LesLocations(Connection conn, int idVelo, int idClient, int numBornetteD, boolean estUnAbonne) {
		// TODO Auto-generated constructor stub
		this.conn = conn;
		this.idVelo = idVelo;
		this.idClient = idClient;
		this.numBornetteD = numBornetteD;
		if(!estUnAbonne) {
			generationCodeSecret(10);
			ajouteUneLocationBD(false);
		} else {
			ajouteUneLocationBD(true);
		}
		
		
	}
	
	
	private void generationCodeSecret(int length) {
		String chars = "azertyuiopmlkjhgfdsqwxcvbn,;:!ų^$*&é'(-č_įā)=123456789*/-+.";
		String codeSecret = "";
		for(int i = 0; i < length; i++) {
			int indice = (int) (Math.random()*chars.length()-1);
			codeSecret += chars.charAt(indice);
		}
		System.out.println("   - Veuillez prendre note du code suivant : " );
		System.out.println("      " + codeSecret);
		System.out.println("    Lorsque vous rendrez votre vélo, saissiez le code sur la borne afin d'ouvrir une bornette");
		this.codeSecret = codeSecret;
	}
	
	/*
	 *  Ajoute une nouvelle location dans BD 
	 *  + trigger dans LesLocations pour initialiser la valeur de dateD a la date d'aujourdhui
	 */
	private void ajouteUneLocationBD(boolean estUnAbonne) {
		CallableStatement cstmt;
	    try {
	    	cstmt = conn.prepareCall ("call ajouteUneLocation (?, ?, ?, ?)");		    
		    cstmt.setInt (1, idClient); 	
		    cstmt.setInt(2, numBornetteD);
		    cstmt.setInt(3, idVelo);
		    if(estUnAbonne) {
		    	cstmt.setString(4, null);
		    } else {
		    	cstmt.setString(4, codeSecret);
		    }		    
		    cstmt.execute ();
		    cstmt.close();
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int trouveVeloAssocierAuCodeSecret() {
		int idVelo = -1;
		Statement r;
		try {
			r = conn.createStatement();			
			ResultSet res = r.executeQuery(
					"SELECT idVelo FROM LesLocations WHERE codeSecret = '" + codeSecret +"'"
					);
			 res.first();
			idVelo = res.getInt(1);		
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idVelo;
	}
	
	public boolean estLeBonVeloLouer(int idVelo) {
		boolean match = false;
		Statement r;
		try {
			r = conn.createStatement();			
			ResultSet res = r.executeQuery(
					"SELECT idVelo FROM LesLocations WHERE idVelo = '" + idVelo +"' AND cout NOT NULL"
					);
			match = res.first();
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return match;
	}
	public void clotureUneLocationBD(boolean abonne) { //HHH
		CallableStatement cstmt;
	    try {
	    	
	    	if(abonne) {
	    		cstmt = conn.prepareCall ("call clotureUneLocation (?)");	
	    		cstmt.setInt (1, numBornetteA); 	
	    	} else {
	    		cstmt = conn.prepareCall ("call clotureUneLocation (?, ?)");	
	    		cstmt.setString (1, codeSecret); 	
	 		    cstmt.setInt(2, numBornetteA);
	    	}
		   
		    cstmt.execute ();
		    cstmt.close();
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setNumBornetteA(int numBornetteA) {
		this.numBornetteA = numBornetteA;
	}
	
	public void afficheVelosClient() {
		Statement r;
		try {
			r = conn.createStatement();			
			ResultSet res = r.executeQuery(
					"SELECT idVelo FROM LesLocations WHERE idClient = '" + idClient + "'"
					);
			while(res.next()) {
				System.out.println("  ID: " + res.getInt("idVelo"));
			}
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	
}

