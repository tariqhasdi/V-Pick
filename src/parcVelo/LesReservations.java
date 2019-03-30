package parcvelo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LesReservations {
	
	private Connection conn;
	
	private int idStation;
	private int idClient;
	private Date dateD;
	private Date dateF;
	
	public LesReservations(Connection conn, int idStation, int idClient) {
		// TODO Auto-generated constructor stub
		this.conn = conn;
		this.idStation = idStation;
		this.idClient = idClient;
		
	}
	
	public int reservationEnConflit() {
		int veloDisponnibles = 0;
		int nbBornettes = 0;
		int veloReserver = 0;
		Statement r;
		try {
			r = conn.createStatement();			
			ResultSet res = r.executeQuery(
					"SELECT count(numBornette) as nbBornettes FROM LesBornettes WHERE idStation = "+idStation 
					);
			 res.first();
			 nbBornettes = res.getInt("nbBornettes");		
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			r = conn.createStatement();			
			ResultSet res = r.executeQuery(
					"SELECT count(*) as veloReserver FROM LesReservations WHERE"
							+ " (dateD<'" + dateD + "' AND dateF>'" + dateF + "') "
							+ " AND (dateD>'" + dateD + "' AND dateD<'" + dateF + "' AND dateF>'" + dateF + "') "
							+ " AND (dateD<'" + dateD + "' AND dateF>'" + dateD + "' AND dateF<'" + dateF + "') "
							+ " (dateD='" + dateD + "' AND dateF='" + dateF + "') "
							+ " (dateD='" + dateD + "' AND dateF>'" + dateF + "') "
							+ " (dateD<'" + dateD + "' AND dateF='" + dateF + "') "
					);
			 res.first();
			 veloReserver = res.getInt("veloReserver");		
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		veloDisponnibles = nbBornettes - veloReserver;
		return veloDisponnibles;
		
	}

}
