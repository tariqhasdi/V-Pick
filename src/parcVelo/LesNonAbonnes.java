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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import utils.LectureClavier;

public class LesNonAbonnes extends LesClients{
	
	LectureClavier scanner;

	public LesNonAbonnes(Connection conn) {
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
		
	 public LesAbonnes abonnement() {
		 LesAbonnes clientAbonne;
		 String nom = "";
		 String prenom = "";
		 String dateNaissance = "";
		 String sexe = "";
		 String adresse = "";
		 String numCB = "";
		 String codeSecret = "";
		 System.out.println("*** Abonnement ***");
		 System.out.print("    + Nom: ");
		 nom = scanner.lireChaine();
		 System.out.print("    + Prenom: ");
		 prenom = scanner.lireChaine();
		 System.out.print("    + dateNaissance: ");
		 dateNaissance = scanner.lireChaine();
		 System.out.print("    + sexe: ");
		 sexe = scanner.lireChaine();
		 System.out.print("    + adresse: ");
		 adresse = scanner.lireChaine();
		 System.out.print("    + numCB: ");
		 numCB = scanner.lireChaine();
		 System.out.print("    + codeSecret: ");
		 codeSecret = scanner.lireChaine();
		 
		 // Crée l'instance d'un client abonne
		 clientAbonne = new LesAbonnes(conn, nom, prenom, dateNaissance, sexe, adresse, numCB, codeSecret);
		 
		 
		 // Set numCb du client non abonne
		 this.setNumCB(numCB);	 
	
		 /*
		  * Ajoute dans LesAbonnes une nouvelle ligne contenant toutes les infos nécessaire
		  */
		clientAbonne.ajouteUnNouveauAbonneBD();		
		
		System.out.println(" Vous venez de vous abonnez, votre numero d'identification est <" + clientAbonne.getIdClient() + "> & mdp <" + clientAbonne.getCodeSecret() + ">");
		return clientAbonne;
		 
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
			 idClient = trouveOuCreeUnClientBD();
			 qtVelosALouerParModeles.forEach((libelle, qt) -> {
				 int cpt = qt;
				 LesBornettes bornette;
				 LesLocations location;
				 while(cpt!=0) {
					bornette = new LesBornettes(conn, station.trouvePremiereBornetteOccupeEnFonctionDuModeleVelo(libelle));
					bornette.libererVeloBD();
					location = new LesLocations(conn, bornette.getIdVelo(), idClient, bornette.getNumBornette(), false);
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


	
	
	



