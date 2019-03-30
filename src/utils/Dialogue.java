package utils;

import java.util.Scanner;
import java.sql.*;

import parcvelo.LesNonAbonnes;
import parcvelo.LesStations;

public class Dialogue {
	
	private Connection conn;
	
	LectureClavier scanner;
	RequetesSql requetesSQL;
	
	public Dialogue(Connection conn) {
		// TODO Auto-generated constructor stub
		this.conn = conn;
		scanner = new LectureClavier();
		requetesSQL = new RequetesSql(conn);
	}
	
	 public LesStations selectionnerStation() {
		 int idStation;
		System.out.println("* A quel station voulez vous vous rendre ? ");
		requetesSQL.afficherToutesLesStations();			
		idStation = scanner.lireEntier("");
		return new LesStations(conn, idStation); 
	 }
	 /* 1 -> Abonne
	  * 0 -> Non Abonne
	  * -1 -> Quitter station
	  */
	 public int phaseIdentification() {
		String reponse = "";
		boolean identificationReussie = false;
		boolean quitterPhaseIdentification = false;
		while(!identificationReussie && !quitterPhaseIdentification) {			
			System.out.println("* Etes-vous abonne au service véPick ? (Oui/Non) ");
			reponse = scanner.lireChaine();		
			while(!reponse.equals("Oui") && !reponse.equals("Non")) {				
				System.out.println("Erreur: veuillez saisir Oui ou Non !");
				reponse = scanner.lireChaine();				
			}
			if(reponse.equals("Oui")) {
				identificationReussie = phaseIdentificationAbonne();
				if(!identificationReussie) {					
					quitterPhaseIdentification = quitterPhaseIdentification();
				}
			} else if(reponse.equals("Non")){
				quitterPhaseIdentification = true;
			}
			
			
		}
		
		if(reponse.equals("Non")) {
			return 0;
		}
		
		if(quitterPhaseIdentification) {
			return -1;
		}
		
		return 1;
	 }
	 
	 public boolean quitterPhaseIdentification() {
		boolean quitterPhaseIdentification = false;
		String reponse = "";
		System.out.println("   Quitter la station ?(Oui/Non)");
		reponse = scanner.lireChaine();
		while(!reponse.equals("Oui") && !reponse.equals("Non")) {
			System.out.println("Erreur: veuillez saisir Oui ou Non !");
			reponse = scanner.lireChaine();
		}
		if(reponse=="Oui") {
			quitterPhaseIdentification = true;
		} 
		return quitterPhaseIdentification;
	 }
	 
	 public boolean phaseIdentificationAbonne() {
		 int choix;
		 boolean match = false;
		 match = verifierIdentiteAbonne();
		 if(!match) {
			 System.out.println("   L'identification a échoué:");
			 System.out.println("      1) Rééssayer");
			 System.out.println("      2) Annuler");
			 choix = scanner.lireEntier("");	
			 while(choix!=1 && choix!=2) {
				 System.out.println("Erreur dans la saisie...");
				 System.out.println("Entrée  1 ou  2");
				 choix = scanner.lireEntier("");
			 }
			 while(!match && choix == 1) {
				 match = verifierIdentiteAbonne();
				 if(!match) {
					 System.out.println("   L'identification a échoué:");
					 System.out.println("      1) Rééssayer");
					 System.out.println("      2) Annuler");
					 choix = scanner.lireEntier("");	
					 while(choix!=1 || choix!=2) {
						 System.out.println("Erreur dans la saisie...");
						 System.out.println("Entrée  1 ou  2");
						 choix = scanner.lireEntier("");
					 }
				 }				 
			 }
		 }
		 if(match){
			 System.out.println("  => Identification réussie, redirection vers votre interface client... ");
		 }
		 return match; // 0 si le client annule ou 1 s'il réussie a se log
	 }
	 
	 public boolean verifierIdentiteAbonne() {
		 int id;
		 String codeSecret = "";
		 boolean match = false;
		 System.out.println("[S'identifier en tant qu'abonné au service VéPick]");
		 System.out.print("  -> Veuillez entrer votre identifiant: ");
		 id = scanner.lireEntier("");
		 System.out.print("  -> Veuillez entrer votre code secret: ");
		 codeSecret = scanner.lireChaine();
		 match = requetesSQL.estUnAbonne(id, codeSecret);
		 return match;
	}
	 
	 public void interfaceNonAbonne(LesNonAbonnes client) {
		 int action;
		 System.out.println("Chargement de l'interface non abonné...");
		 // Il faut ajouter dans la table LesNonAbonnes, une nouvel ligne
		 System.out.println("Effectuer une action:");
		 System.out.println("   1) S'abonner au service VéPick");
		 System.out.println("   2) Louer un ou plusieurs vélos");
		 System.out.println("   3) Rendre un ou plusieurs vélos");
		 System.out.println("   4) Quitter la station");
		 action = scanner.lireEntier("");
		 switch (action) {
		case 1:
			phaseAbonnement();
			break;
		case 2:
			
			break;
		

		default:
			break;
		}
	 }
	 
	 public void phaseAbonnement() {
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
		 System.out.print("\n     + Prenom: ");
		 prenom = scanner.lireChaine();
		 System.out.print("    + dateNaissance: ");
		 dateNaissance = scanner.lireChaine();
		 System.out.print("\n     + sexe: ");
		 sexe = scanner.lireChaine();
		 System.out.print("    + adresse: ");
		 adresse = scanner.lireChaine();
		 System.out.print("\n     + numCB: ");
		 numCB = scanner.lireChaine();
		 System.out.print("\n     + codeSecret: ");
		 codeSecret = scanner.lireChaine();
		 
		 
		 
	 }
	 
}
