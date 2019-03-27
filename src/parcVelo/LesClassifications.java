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


import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;

public class LesClassifications {
	
		private Connection conn;

		private int idStation;
		private Date heureD;
		private Date heureF;
		private String today;
		private Classifications classification; 
		
		public LesClassifications(Connection conn, int idStation) {
			// TODO Auto-generated constructor stub
			this.conn = conn;
			this.idStation = idStation;
			this.today = getDay();
			classification = Classifications.VNUL;
			initClassification();			
		}
		
		public Classifications getClassification() {
			return classification;
		}
		
		public void initClassification() {
			String classification = "";
			Statement r;
			try {
				r = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE); // Sans ces parametres JDBC crash		
				ResultSet res = r.executeQuery(
						"SELECT classification FROM LesClassifications" 
						+ " WHERE idStation = " + idStation +
							" AND to_char(heureD,'HH24:MI:SS') <= to_char(SYSDATE, 'HH24:MI:SS')" +
							" AND to_char(heureF,'HH24:MI:SS') >= to_char(SYSDATE, 'HH24:MI:SS')"+
							" AND jour =  '" + today + "'"
						);
				res.first();
				classification = res.getString(1);
				r.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.classification.setEtat(classification);
		}
		
		public String getDay(){
	        GregorianCalendar gCalendar = new GregorianCalendar();
	        int today = gCalendar.get(gCalendar.DAY_OF_WEEK);
	        switch (today){
	            case 1 : 
	                return "Dimanche";
	            case 2 :
	                return "Lundi";
	            case 3 : 
	                return "Mardi";
	            case 4 : 
	                return "Mercredi";
	            case 5 :
	                return "Jeudi";
	            case 6 :
	                return "Vendredi";
	            case 7 :
	                return "Samedi";
	            default:
	                return "erreur";
	        }
	    }
}

