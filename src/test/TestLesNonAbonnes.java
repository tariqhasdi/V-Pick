/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author Tariq
 */

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import parcVelo.LesAbonnes;
import parcVelo.LesBornettes;
import parcVelo.LesClients;
import parcVelo.LesLocations;
import parcVelo.LesStations;
import utils.LectureClavier;

public class TestLesNonAbonnes extends LesClients{
	
	LectureClavier scanner;

	public TestLesNonAbonnes(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}
	
	
	
	 public int monInterface() {
		 int action;
		 System.out.println("Chargement de l'interface non abonnee...");
		 System.out.println("Effectuer une action:");
		 System.out.println("   1) S'abonner au service VePick"); 
		 System.out.println("   2) Louer velos"); 
		 System.out.println("   3) Rendre un velo");
		 System.out.println("   4) Signaler une panne");
		 System.out.println("   5) Quitter la station");

		 action = scanner.lireEntier("");
		 return action;
	 }		
		
	 public void abonnement() {
		 LesAbonnes clientAbonne;	
		 
		 // Crée l'instance d'un client abonne
		 // clientAbonne = new LesAbonnes(conn, "Jean", "Michel", "30/01/1999", "M", "22 rue trefles", "159753", "147K");
		 clientAbonne = new LesAbonnes(conn, "Tariq", "Michel", "30/01/1999", "F", "22 rue tulipes", "04745333231254", "12ML");
		 
		 
		 // Set numCb du client non abonne
		 this.setNumCB("04745333231254");	
		 
		 /*
		  * Ajoute dans LesAbonnes une nouvelle ligne contenant toutes les infos nécessaire
		  */
		 clientAbonne.ajouteUnNouveauAbonneBD();		 
		 
	 }
	
	 @Override
	 public Map<String, Integer> empruntVelo(LesStations station) {
		
		 Map<String, Integer> qtVelosALouerParModeles  = new HashMap<String, Integer>();
		 qtVelosALouerParModeles = super.empruntVelo(station);
		 if(!qtVelosALouerParModeles.isEmpty()) {
			 // Demande au client d'entrer sa CB pour payer 
			 System.out.println("   * Paiement * ");
			 System.out.print("    + Numero de carte bancaire: ");
			 numCB = LectureClavier.lireChaine();
			 // Cherche dans la base de donne (BD) LesClients, l'id du client .
			 // S'il ne le trouve pas, il dois créer un nouveau client et renvoyer son idClient
			 trouveOuCreeUnClientBD();
			 qtVelosALouerParModeles.forEach((libelle, qt) -> {
				 int cpt = qt;
				 LesBornettes bornette;
				 LesLocations location;
				 while(cpt!=0) {
					bornette = new LesBornettes(conn, station.trouvePremiereBornetteOccupeEnFonctionDuModeleVelo(libelle));
					bornette.libererVeloBD();
					location = new LesLocations(conn, station.getIdStation(), idClient, bornette.getNumBornette(), false);
					cpt--;
				}
			});
			 System.out.println(" Merci de nous avoir fait confiance, bonne route ! :)");
		 } 		
		 return qtVelosALouerParModeles;		
	}
	 

	 public int rendreVelo(LesStations station) {
		int idVeloAssocierAuCodeSecret;
		int idVeloARendre;
		String codeSecret = "";
		LesLocations location;
		int numBornette;
		LesBornettes bornette;
		System.out.println("*** Rendre velo ***");
		System.out.print("   + Id velo: ");
		idVeloARendre = LectureClavier.lireEntier("");
		System.out.println("");
		System.out.print("   + Code secret: ");
		codeSecret = LectureClavier.lireChaine();
		location = new LesLocations(conn, codeSecret);
		idVeloAssocierAuCodeSecret = location.trouveVeloAssocierAuCodeSecret();
		if(idVeloARendre!=idVeloAssocierAuCodeSecret) {
			System.out.println("Erreur le velo ne correspond pas a la location identifié au code <" + codeSecret);
			System.out.println("Redirection vers le menu...");
			return -1;
		}
		numBornette = station.trouvePremiereBornetteLibre();
		location.setNumBornetteA(numBornette);
		bornette = new LesBornettes(conn, numBornette, idVeloAssocierAuCodeSecret);
		bornette.attacherVeloBD();
		location.clotureUneLocationBD(false);
		System.out.println(" Velo " + idVeloARendre + " attachez à la bornette !");
		return 1;
		
	}
	
}

