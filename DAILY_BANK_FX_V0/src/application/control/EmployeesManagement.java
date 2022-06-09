package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.AccessEmploye;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployeesManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private EmployeManagementController emc;

	
	public EmployeesManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmployeManagementController.class.getResource("employemanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des employées");
			this.primaryStage.setResizable(false);

			this.emc = loader.getController();
			this.emc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Permet d'afficher la boite de dialogue de management du client 
	 */
	public void doEmployeManagementDialog() {
		this.emc.displayDialog();
	}
	
	

	/*
	 * Permet de modifier les informations li�es � un client
	 * @param c : le client
	 * @return le client
	 */
	public Employe modifierEmploye(Employe employeMod) {
		EmployeEditorPane cep = new EmployeEditorPane(this.primaryStage, this.dbs);
		Employe result = cep.doClientEditorDialog(employeMod, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessEmploye ae = new AccessEmploye();
				ae.updateEmploye(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}
	

	/*
	 * Permet de cr�er un nouveau client
	 * @return le client cr��
	 */
	public Employe nouveauEmploye() {
		Employe employe;
		EmployeEditorPane cep = new EmployeEditorPane(this.primaryStage, this.dbs);
		employe = cep.doClientEditorDialog(null, EditionMode.CREATION);
		if (employe != null) {
			try {
				AccessEmploye ae = new AccessEmploye();

				ae.insertEmploye(employe);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				employe = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				employe = null;
			}
		}
		return employe;
	}


	/*
	 * Permet de trouver les clients qui sont li�s � un certain compte
	 * @param numCompte : le num�ro de compte
	 * @param _debutNom : le d�but du nom du client
	 * @param _debutPrenom : le d�but du pr�nom du client
	 * @return une ArrayList de clients 
	 */
	public ArrayList<Employe> getlisteComptes(int _idEmploye, String _debutNom, String _debutPrenom) {
		ArrayList<Employe> listeEmploye = new ArrayList<>();
		try {
			// Recherche des clients en BD. cf. AccessClient > getClients(.)
			// numCompte != -1 => recherche sur numCompte
			// numCompte != -1 et debutNom non vide => recherche nom/prenom
			// numCompte != -1 et debutNom vide => recherche tous les clients

			AccessEmploye ae = new AccessEmploye();
			listeEmploye = ae.getEmployes(this.dbs.getEmpAct().idAg, _idEmploye, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeEmploye = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeEmploye = new ArrayList<>();
		}
		return listeEmploye;
	}

	public void supprimerEmploye(Employe employeSuppr) {
		
		if (employeSuppr != null) {
			try {
				AccessEmploye ae = new AccessEmploye();
				ae.supprEmploye(employeSuppr);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				employeSuppr = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				employeSuppr = null;
			}
		}
		
	}
}
