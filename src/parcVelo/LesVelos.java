package parcvelo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

public class LesVelos {
	
	private Connection conn;
	private int idVelo;
	private Date dateMiseEnService;
	private Etats etat;
	private boolean estEnMaintenance;
	private int idModele;
	
	public LesVelos(Connection conn, int idVelo) {
		// TODO Auto-generated constructor stub
		this.conn = conn;
		this.idVelo = idVelo;
	}
	
	
	
	public void setEstEnMaintenance(boolean estEnMaintenance) {
		this.estEnMaintenance = estEnMaintenance;
	}
	
	public int getIdVelo() {
		return idVelo;
	}
	
	
	

}
