/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Tariq
 */

import java.sql.*;

public class RequetesSql {
	
	Connection conn;
	
	public RequetesSql(Connection conn) {
		// TODO Auto-generated constructor stub
		this.conn = conn;
	}
	
	public void afficherToutesLesStations() {
		int id = 0;
		Statement r;
		try {
			r = conn.createStatement();			
			ResultSet res = r.executeQuery(
					"SELECT * FROM LesStations"
					);
			while(res.next()) {
				System.out.println(res.getString("idstation") + "]" + res.getString("adresse"));
				id ++;
			}
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean estUnAbonne(int id, String code) {
		boolean match = false;
		Statement r;
		try {
			r = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE); // Sans ces parametres JDBC crash
			ResultSet res = r.executeQuery(
					"SELECT * FROM LesAbonnes WHERE idclient = " + id + " AND codesecret = '" + code + "'"
					);
			match = res.first(); 
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return match;
	}
	
	public void ajouteUnAbonne(String nom, String prenom, String dateN, String sexe, String adresse, String numCB, String codeSecret, Connection conn) {
	    CallableStatement cstmt;
	    try {
	    	cstmt = conn.prepareCall ("call addAbonne (?, ?, ?, ?, ?, ?, ?)");		    
		    cstmt.setString (1, nom); 
		    cstmt.setString (2, prenom);
		    cstmt.setString (3, dateN);
		    cstmt.setString (4, sexe);
		    cstmt.setString (5, adresse);
		    cstmt.setString (6, numCB);
		    cstmt.setString (7, codeSecret);
		    cstmt.execute ();
		    cstmt.close();
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	}
	
}

