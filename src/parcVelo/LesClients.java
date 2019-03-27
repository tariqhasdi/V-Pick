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
import java.util.HashMap;
import java.util.Map;

import utils.LectureClavier;

public class LesClients {
	
	protected  int idClient;
	protected String numCB;
	
	protected Connection conn;
	
	public LesClients(Connection conn) {
		this.conn = conn;
	}
	
	public void setNumCB(String numCB) {
		this.numCB = numCB;
	}
	
	

	 /*
	  *  Vérifier si le client n'es pas deja connu dans la table LesClients grace a son numCB
	  *  S'il n'es pas connu, on l'ajoute
	  */
	public void ajouteUnNouveauClientBD() {
		  CallableStatement cstmt;
		    try {
		    	cstmt = conn.prepareCall ("call ajouteUnClient (?)");		    
			    cstmt.setString (1, numCB); 			   
			    cstmt.execute ();
			    cstmt.close();
		    } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); //ii
			}
		 
		}
	
	public Map<String, Integer> empruntVelo(LesStations station) {
		 Map<String, Integer> qtVelosALouerParModeles  = new HashMap<String, Integer>();
		 Map<String, Integer> qtVelosParModeles;
		 String modele;
		 int quantite;
		 int action;
		 boolean quitter = false;
		 String continuerLocationsDeVelos = "Oui";
		 System.out.println("*** Emprunt velos ***");
		 System.out.println(" # Station <" + station.getIdStation() + "> - Classification <" + station.getClassification().getEtat() + "> #");
		
			 System.out.println("   + Modeles disponibles: ");
			 qtVelosParModeles = station.afficherModelesDispoAvectQt();	
			 qtVelosParModeles.forEach((libelle, qt) -> {
				 System.out.println("   - " + libelle + " : " + qt + " velos");
			 });
			 System.out.print("  - Quel modeles voulez vous ?: ");
			 modele = LectureClavier.lireChaine();
			 while(!qtVelosParModeles.containsKey(modele)) {
				 System.out.print("  Erreur: Saisir un modele disponnible svp: ");
				 modele = LectureClavier.lireChaine();
			 }				 
			 System.out.print("  - Nombre de velos pour ce modele: ");
			 quantite = LectureClavier.lireEntier("");
			 System.out.println("QT: " + quantite);
			 while(quantite > qtVelosParModeles.get(modele) || quantite < 0) {  
				 System.out.print(" Erreur: Saisir une quantite coherente svp: "); 
				 quantite = LectureClavier.lireEntier("");
				 System.out.println("QT: " + quantite);
			 }
			 qtVelosALouerParModeles.put(modele, quantite);
			 System.out.println("  Action: ");
			 System.out.println("    1) Terminer location");
			 System.out.println("    2) Ajouter un autre modele dans votre panier");
			 System.out.println("    3) Annuler location");
			 action = LectureClavier.lireEntier("");
			 while(!quitter) {
				 switch (action) {
				 	case 2:				 		
						 System.out.print("  - Quel autres modeles voulez vous ?: ");
						 modele = LectureClavier.lireChaine();
						 while(!qtVelosParModeles.containsKey(modele) || qtVelosALouerParModeles.containsKey(modele) ) {
							 System.out.print("  Erreur: Saisir un modele disponnible svp: ");
							 modele = LectureClavier.lireChaine();
						 }				 
						 System.out.print("  - Nombre de velos pour ce modele: ");
						 quantite = LectureClavier.lireEntier("");
						 while(quantite > qtVelosParModeles.get(modele)) {
							 System.out.print(" Erreur: Saisir une quantite coherente svp: "); 
							 quantite = LectureClavier.lireEntier("");
						 }
						 qtVelosALouerParModeles.put(modele, quantite);
						 System.out.println("  Action: ");
						 System.out.println("    1) Terminer location");
						 if(qtVelosParModeles.size()==qtVelosALouerParModeles.size()) {
							 System.out.println("   2) Annuler location");
							 action = LectureClavier.lireEntier("");
							 if(action==2) {
								 action = 3;							 
							 } else {
								 action = -1;
							 }
						 } else {
							 System.out.println("    2) Ajouter un autre modele dans votre panier");
							 System.out.println("    3) Annuler location");
							 action = LectureClavier.lireEntier("");							 
						 }					 
						
				 		break;
					case 3:
						qtVelosALouerParModeles.clear();
						System.out.println("  Votre location a été annuler...");
						quitter = true;
						break;
		
					default:
						System.out.println("  Ouverture des bornettes en cours...");
						quitter = true;
						break;
				}
			 }
			
				 
			/* System.out.print("    Choissisez un modele: ");
				 modele = LectureClavier.lireChaine();
				 while(!qtVelosParModeles.containsKey(modele)) {
					 System.out.println("   Erreur: Veuiller choisir un modele qui existe et qui disponnible dans cette station (id: " + station.getIdStation() + ")");
					 System.out.print("    Choissisez un modele: ");
					 modele = LectureClavier.lireChaine(); 
				 }
				 if(qtVelosALouerParModeles.containsKey(modele)) {
					 qtVelosALouerParModeles.put(modele, qtVelosALouerParModeles.get(modele)+1);
				 } else {
					 qtVelosALouerParModeles.put(modele, 1);
				 }			
				 System.out.print(" Voulez vous louer un autre velo ? (Oui/Non) : ");
				 continuerLocationsDeVelos = LectureClavier.lireChaine();
				 while(!continuerLocationsDeVelos.equals("Oui") && !continuerLocationsDeVelos.equals("Non")){
					 System.out.println("Saisir Oui ou Non... ");
					 System.out.print(" Voulez vous louer un autre velo ? (Oui/Non) : ");
					 continuerLocationsDeVelos = LectureClavier.lireChaine();
				 }
			 } else {
				 System.out.println("Aucun velos disponnible dans la station, veuillez aller a une autre station ou revenir plus tard.");
				 return qtVelosParModeles;
			 }			*/
		
		 
		 return qtVelosALouerParModeles;	 
	 }
	
	public int trouveOuCreeUnClientBD() {
		 /*
		  *  	Trouve l'idClient en fonction de numCB
		  *  	S'il ne le trouve pas, il dois créer un nouveau client et renvoyer son idClient
		  *  
		  */
		System.out.println("Client CB: " + numCB);
		int idClient = -1;			
		 Statement r;
			try {
				r = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE); // Sans ces parametres JDBC crash			
				ResultSet res = r.executeQuery(
						"SELECT idClient FROM LesClients WHERE numCb = '" + numCB + "'"
						);
				if(res.first()) {
					idClient = res.getInt("idClient");
				} else {
					ajouteUnNouveauClientBD();
					res = r.executeQuery(
							"SELECT idClient FROM LesClients WHERE numCb = " + numCB
							);
					res.first();
					idClient = res.getInt("idClient");						
				}
				
				r.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	 
			if(idClient == -1) {
				System.out.println("Erreur ajout d'un nouveau client echec");
			}
			return idClient;
	 }

	public void signalerPanne(LesStations station) {
		LesLocations location = new LesLocations(conn, idClient);
		int idVeloEnPanne;
		LesVelos veloEnPanne;
		System.out.println("*** Panne ***");
		System.out.println(" Selectionne l'id du velo en panne: ");
		location.afficheVelosClient();
		idVeloEnPanne = LectureClavier.lireEntier("");
		veloEnPanne = new LesVelos(conn, idVeloEnPanne);
		station.envoyerVeloEnMaintenanceBD(veloEnPanne);
		
	}

}

