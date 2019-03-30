package parcvelo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import utils.LectureClavier;
import utils.RequetesSql;

public class LesAbonnes extends LesClients {	

	 private String nom = "";
	 private String prenom = "";
	 private String dateNaissance = "";
	 private String sexe = "";
	 private String adresse = "";
	 private String codeSecret = "";
	 private int prime;
	 
	 public LesAbonnes(Connection conn) {
		 super(conn);
	 }
	 
	

	public LesAbonnes(Connection conn, String nom, String prenom, String dateN, String sexe, String adresse, String numCB, String codeSecret) {
		super(conn);
		this.nom = nom;
		this.prenom = prenom;
		this.dateNaissance = dateN;
		this.sexe = sexe;
		this.adresse = adresse;		
		this.codeSecret = codeSecret;
		setNumCB(numCB);
		ajouteUnNouveauClientBD();
		this.idClient = trouveDernierClient();
		System.out.println("IDCLIENT: " + idClient);
		// TODO Auto-generated constructor stub
	}
	
	
	public int getIdClient() {
		return trouveDernierClient();
	}
	
	public String getCodeSecret() {
		return codeSecret;
	}
	
	public boolean ajouteUnNouveauAbonneBD() {
		System.out.println("TEST : IDCLIENT -> " + idClient);
		boolean success = false;
	    CallableStatement cstmt;
	    try {
	    	cstmt = conn.prepareCall ("call ajouteUnAbonne (?, ?, ?, ?, ?, ?, ?, ?)");	
	    	cstmt.setInt(1, idClient);
		    cstmt.setString (2, nom); 
		    cstmt.setString (3, prenom);
		    cstmt.setString (4, dateNaissance);
		    cstmt.setString (5, sexe);
		    cstmt.setString (6, adresse);
		    cstmt.setString (7, numCB);
		    cstmt.setString (8, codeSecret);
		    cstmt.execute ();
		    success = true;
		    cstmt.close();
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return success;
	 
	}
	
	 public int monInterface() {
		 int action;
		 System.out.println("Chargement de l'interface abonnee...");
		 System.out.println("Effectuer une action:");
		 System.out.println("   1) Reserver velo"); 
		 System.out.println("   2) Louer velo"); 
		 System.out.println("   3) Rendre un velo");
		 System.out.println("   4) Signaler une panne");
		 System.out.println("   5) Quitter la station");

		 action = LectureClavier.lireEntier("");
		 return action;
	 }		
	 
	 
	 public void reservation(LesStations stationAccueil) {
		 int velosDispo = 0;
		 LesStations station;
		 LesReservations reservation;
		 Date dateD;
		 Date dateF;
	     SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy") ; 
		 System.out.println("*** RESERVATION ***");
		 System.out.println(" + Choisissez une station: ");
		 RequetesSql r = new RequetesSql(conn);
		 r.afficherToutesLesStations();
		 station = new LesStations(conn, LectureClavier.lireEntier(""));
		 reservation = new LesReservations(conn, station.getIdStation(), idClient);
		 System.out.print(" + Date de debut: ");
		 try {
			dateD = sdf.parse(LectureClavier.lireChaine());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.print(" + Date de fin: ");
		 try {
			dateF = sdf.parse(LectureClavier.lireChaine());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 velosDispo = reservation.reservationEnConflit();
		 
		 
		if(velosDispo==0) {
			int action;
			System.out.println("- Erreur: Il n'y a plus de velos disponnible dans cette plage horaire");
			System.out.println("Procedure en cours d'annulation ...");			
		} else {
			System.out.println(" =* Il y a " + velosDispo + " pour cette plage horaire");
			System.out.println(" Reservation effectue !");
		}
		 
	 }
	 
	 @Override
	public Map<String, Integer> empruntVelo(LesStations station) {
		 Classifications classifications = station.getClassification();
		 Map<String, Integer> qtVelosALouerParModeles  = new HashMap<String, Integer>();
		 qtVelosALouerParModeles = super.empruntVelo(station);
		 if(!qtVelosALouerParModeles.isEmpty()) {	
			 qtVelosALouerParModeles.forEach((libelle, qt) -> {
				 int cpt = qt;
				 LesBornettes bornette;
				 LesLocations location;
				 while(cpt!=0) {
					bornette = new LesBornettes(conn, station.trouvePremiereBornetteOccupeEnFonctionDuModeleVelo(libelle));
					bornette.libererVeloBD();
					location = new LesLocations(conn, station.getIdStation(), idClient, bornette.getNumBornette(), true);
					cpt--;
				}
			});
			 if(classifications == Classifications.VPLUS) {
					System.out.println("Vous beneficiez d'un prime de 1min car vous empruntez un velo dans une station Vplus");
					incrementePrimeBD();
				} 
			 System.out.println(" Merci de nous avoir fait confiance, bonne route ! :)");
		 } 		
		 return qtVelosALouerParModeles;		
	}
	 
	 public int rendreVelo(LesStations station) {
		 	Classifications classifications = station.getClassification();
			int idVeloARendre;
			LesLocations location;
			int numBornette;
			LesBornettes bornette;
			System.out.println("*** Rendre velo ***");
			System.out.println(" ## Station <" + station.getIdStation() + "> - Classification <" + classifications.getEtat()+ ">");
			System.out.print("   + Id velo: ");
			idVeloARendre = LectureClavier.lireEntier("");
			System.out.println("");			
			location = new LesLocations(conn, idClient);
			
			if(!location.estLeBonVeloLouer(idVeloARendre)) {
				System.out.println("Erreur le velo ne correspond pas a la location identifié au code <" + codeSecret);
				System.out.println("Redirection vers le menu...");
				return -1;
			}
			numBornette = station.trouvePremiereBornetteLibre();
			location.setNumBornetteA(numBornette);
			bornette = new LesBornettes(conn, numBornette, idVeloARendre);
			bornette.attacherVeloBD();
			location.clotureUneLocationBD();			
			System.out.println(" Velo " + idVeloARendre + " attachez à la bornette  " + numBornette + " !");
			if(classifications == Classifications.VMOINS) {
				System.out.println("Vous beneficiez d'un prime de 1min car vous rendez un velo dans une station Vmoins");
				incrementePrimeBD();
			} 
			return 1;
	 }
	 
	 public void incrementePrimeBD() {
		 int _prime = 0;
		 Statement r;
		 // Trouve la prime de cet abonne
			try {
				r = conn.createStatement();			
				ResultSet res = r.executeQuery(
						"SELECT prime FROM LesAbonnes WHERE idClient = " + idClient 
						);
				 res.first();
				 _prime = res.getInt("prime");		
				r.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  // Incremente la prime	
			_prime++;
			try {
				r = conn.createStatement();			
				ResultSet res = r.executeQuery(
						"UPDATE LesAbonnes SET prime = "+ _prime + " WHERE idClient = " + idClient
						);
				r.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	
	
	

}
