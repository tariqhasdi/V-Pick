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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import utils.LectureClavier;

public class LesStations {
	
	private Connection conn;
	
	private int idStation;
	private String adresse;
		
	public LesStations(Connection conn, int idStation) {
		this.conn = conn;
		this.idStation = idStation;
		// SQL pour obtenir l'adresse 
	}
	
	
	public int getIdStation() {
		return idStation;
	}
	
	public Map<String, Integer> afficherModelesDispoAvectQt() {
		Map<String, Integer> qtVelosParModeles  = new HashMap<String, Integer>();
		Statement r;
		try {
			r = conn.createStatement();			
			ResultSet res = r.executeQuery(
					"SELECT libelle, count(libelle) as qt FROM LesModeles NATURAL JOIN LesVelos NATURAL JOIN LesBornettes WHERE idStation = " + idStation + " GROUP BY libelle"
					);
			while(res.next()) {
				qtVelosParModeles.put(res.getString("libelle"), res.getInt("qt"));				
			}
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return qtVelosParModeles;
	}

	/* 
	 *  La position des bornettes est en fonction de leurs id.
	 *  Par exemple, la bornette 15 est plus proche que la bornette 25
	 */
	public int trouvePremiereBornetteOccupeEnFonctionDuModeleVelo(String libelle) {
		int numBornette = -1;
		Statement r;
		try {
			r = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE); // Sans ces parametres JDBC crash				
			ResultSet res = r.executeQuery(
					"SELECT min(numBornette) FROM LesBornettes NATURAL JOIN LesModeles WHERE idStation = " + idStation + " AND libelle = '" + libelle +"' AND idVelo IS NOT NULL"
					);
			 res.first();
			numBornette = res.getInt(1);		
			System.out.println("Num bornette occupé: " + numBornette );
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(numBornette==-1) {
			System.out.println("Echec de la requete: trouvePremiereBornetteOccupeEnFonctionDuModeleVelo");
		}
		return numBornette;
	}
	
	public int trouvePremiereBornetteLibre() {
		int numBornette = -1;
		Statement r;
		try {
			r = conn.createStatement();			
			ResultSet res = r.executeQuery(
					"SELECT min(numBornette) FROM LesBornettes WHERE idStation = " + idStation + "' AND idVelo  NULL"
					);
			res.first();
			numBornette = res.getInt(1);		
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(numBornette==-1) {
			System.out.println("Echec de la requete: trouvePremiereBornetteOccupeEnFonctionDuModeleVelo");
		}
		return numBornette;
	}
	
	public void envoyerVeloEnMaintenanceBD(LesVelos velo) {
		boolean signalerPanneATemps = false;
		int dureeDeLocation = 0;
		double cout = 0;
		velo.setEstEnMaintenance(true);
		 CallableStatement cstmt;
		    try {
		    	cstmt = conn.prepareCall ("call envoyerVeloEnMaintenance (?)");		    // Prend idVelo et update dans LEsVelos le bool estEnMaintenance a true
			    cstmt.setInt (1, velo.getIdVelo()); 			   
			    cstmt.execute ();
			    cstmt.close();
		    } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    /*
		     * Il faut cloturer la location
		     * Si la difference entre la date de début de location et la date actuel (signalement panne)
		     *  est plus petit que 3 min alors on paye pas
		     *  
		     */
		    Statement r;
			try {
				r = conn.createStatement();			
				ResultSet res = r.executeQuery(
						"SELECT DATEPART(mi, DATEDIFF(dateD, SYSDATE)) as dateDiff FROM LesLocations WHERE idVelo = " + velo.getIdVelo() + " AND cout is NULL"
						);
				if(res.first()) {
					dureeDeLocation = res.getInt("dateDiff");
					if(dureeDeLocation < 3) {
						System.out.println("Temps < 3 , donc pas vous n'etes pas debite...");
						signalerPanneATemps = true;
					} else {
						System.out.println(" Vous avez mis trop de temps a signaler la panne de ce vélo, vous devez donc payer la demi heure");
					}
				} else {
					System.out.println("Erreur aucune location en cours pour ce velo");
				}
				
				r.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			   if(signalerPanneATemps) {
				   try {
						r = conn.createStatement();			
						ResultSet res = r.executeQuery(
								"UPDATE LesLocations SET dateF = SYSDATE, cout = 0 WHERE idVelo = " + velo.getIdVelo() + " AND cout IS NULL"
								);
						if(res.first()) {
							if(res.getInt("dateDiff") < 3) {
								System.out.println("Votre location pour le velo " + velo.getIdVelo() + " est cloture sans vous facturez");
								signalerPanneATemps = true;
							}
						} else {
							System.out.println("Erreur aucune location en cours pour ce velo");
						}
						
						r.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    
			   } else { // TROP TARD > 3 min
				   
				   try {
						r = conn.createStatement();			
						ResultSet res = r.executeQuery(
								"SELECT cout FROM LesModeles NATURAL JOIN LesVelos WHERE idVelo = " + velo.getIdVelo()
								);				
						res.first();
						cout = res.getInt("cout");
						r.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   cout *= (dureeDeLocation / 30);
				   
				   try {
						r = conn.createStatement();			
						ResultSet res = r.executeQuery(
								"UPDATE LesLocations SET dateF = SYSDATE, cout = " + cout + " WHERE idVelo = " + velo.getIdVelo() + " AND cout IS NULL"
								);					
						System.out.println("Votre location pour le velo " + velo.getIdVelo() + " est cloture paiement exigee");
						r.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    
			   }
				
		 
	}
	
	public Classifications getClassification() {
		 Date actuelle = new Date();
		 DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		 String date = dateFormat.format(actuelle);
		 LesClassifications classification= new LesClassifications(conn, idStation);
		 return classification.getClassification();		
	}

}

