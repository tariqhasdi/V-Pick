package main;

import java.sql.*;

import parcvelo.LesAbonnes;
import parcvelo.LesClients;
import parcvelo.LesNonAbonnes;
import parcvelo.LesStations;
import utils.Dialogue;
import utils.LectureClavier;

public class InterfaceClient {

	static final String CONN_URL = "jdbc:oracle:thin:@im2ag-oracle.e.ujf-grenoble.fr:1521:im2ag";

	// Ne pas oublier d'anomymiser le PASSWD avant de rendre votre travail

		static final String USER = "hasdit";
                static final String PASSWD = "az1ER2t3";                                                                                                    //static final String PASSWD = "DarkFire123";

		static Connection conn; 
		
		
	    public static void main(String args[]) {
	    	
	    	Dialogue dialogue;

	        try {
	        
	        	// Enregistrement du driver Oracle
				System.out.print("Loading Oracle driver... ");
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				System.out.println("loaded");

				// Etablissement de la connection
				System.out.print("Connecting to the database... ");
				conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
				System.out.println("connected");
	  	    
				conn.setAutoCommit(true);
				
				// Debut 	
				dialogue = new Dialogue(conn);
				LesStations stationInitial;
				LesClients client;
				LesAbonnes abonne = null;
				LesNonAbonnes nonAbonne = null;
				
				System.out.println("[INITIALISATION]");				
				
				
				stationInitial = dialogue.selectionnerStation();
				
				System.out.println("C'est parti -> ");
				System.out.println("------------ INTERFACE CLIENT ------------");
				int action;
				
				int identification = dialogue.phaseIdentification();					
				
				switch (identification) {			
						
					case 0: // Non Abonne
						nonAbonne = new LesNonAbonnes(conn);
						//TestLesNonAbonnes nonAbonne = new TestLesNonAbonnes(conn);
						action = nonAbonne.monInterface();
						
						switch (action) {
						case 1:
							//nonAbonne.abonnement();							
							if((abonne = nonAbonne.abonnement()) != null) {
								identification = 1;
							}
							break;
						case 2:
							nonAbonne.empruntVelo(stationInitial);
							break;
						case 3:
							nonAbonne.rendreVelo(stationInitial);
							break;
						case 4:
							nonAbonne.signalerPanne(stationInitial);
							break;
						default:
							break;
						}
						//dialogue.interfaceNonAbonne(client);
						break;						
					case 1: // Abonne
						if(abonne == null) {
							abonne = new LesAbonnes(conn);
						}
						
						action = abonne.monInterface();
						
						switch (action) {
						case 1:
							abonne.reservation(stationInitial);
							break;
						case 2:
							abonne.empruntVelo(stationInitial);
							break;
						case 3:
							abonne.rendreVelo(stationInitial);
							break;
						case 4:
							abonne.signalerPanne(stationInitial);
							break;
						default:
							break;
						}
						break;
						
					case -1: // Annulation
	
					default:
						break;
				}
				
				
				
				
				// Fin 

			   
		 		conn.close(); 
		 	    
		  	    System.out.println("bye.");
	  	    
	  	    // traitement d'exception
	          } catch (SQLException e) {
	              System.err.println("failed");
	              System.out.println("Affichage de la pile d'erreur");
	  	          e.printStackTrace(System.err);
	              System.out.println("Affichage du message d'erreur");
	              System.out.println(e.getMessage());
	              System.out.println("Affichage du code d'erreur");
	  	          System.out.println(e.getErrorCode());	    

	          }
	     }
	    
	    
	   

}
