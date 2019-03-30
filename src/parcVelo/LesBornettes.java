package parcvelo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LesBornettes {
	
	private Connection conn;
	
	private int numBornette;
	private int idVelo;
	private int idStation;
	private Etats etat;
	
	public LesBornettes(Connection conn, int numBornette, int idVelo) {
		// TODO Auto-generated constructor stub
		this.conn = conn;
		this.numBornette = numBornette;
		this.idVelo = idVelo;
	}
	
	public LesBornettes(Connection conn, int numBornette) {
		// TODO Auto-generated constructor stub
		this.conn = conn;
		this.numBornette = numBornette;
		initIdVelo();
	}
	
	public void initIdVelo() {
		int idVelo = -1;
		Statement r;
		try {
			r = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE); // Sans ces parametres JDBC crash				
			ResultSet res = r.executeQuery(
					"SELECT idVelo FROM LesBornettes WHERE numBornette = '" + numBornette + "'"
					);
			res.first();
			idVelo = res.getInt(1);	
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(idVelo==-1) {
			System.out.println("Echec de la requete: initIdVelo");
		} else {
			this.idVelo = idVelo;
		}
		
	}
	
	public void libererVeloBD() {
		CallableStatement cstmt;
	    try {
	    	cstmt = conn.prepareCall ("call libererVelo (?)");		    
		    cstmt.setInt (1, numBornette); 			   
		    cstmt.execute ();
		    cstmt.close();
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	}
	
	public int getIdVelo() {
		return idVelo;
	}
	
	public int getNumBornette() {
		return numBornette;
	}
	
	public void attacherVeloBD() {
		CallableStatement cstmt;
	    try {
	    	cstmt = conn.prepareCall ("call attacherVelo (?, ?)");		    
		    cstmt.setInt (1, numBornette); 	
		    cstmt.setInt (2, idVelo);
		    cstmt.execute ();
		    cstmt.close();
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
