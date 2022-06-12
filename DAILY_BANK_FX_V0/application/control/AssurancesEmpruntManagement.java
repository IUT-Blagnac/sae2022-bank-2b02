package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.AssurancesEmpruntManagementController;
import application.view.ClientsManagementController;
import application.view.PrelevementsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Prelevement;
import model.orm.AccessClient;
import model.orm.AccessPrelevementAutomatique;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class AssurancesEmpruntManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private AssurancesEmpruntManagementController amc;

	
	public AssurancesEmpruntManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(AssurancesEmpruntManagementController.class.getResource("asssurancesempruntmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Simulation d'une assurance d'emprunt");
			this.primaryStage.setResizable(false);

			this.amc = loader.getController();
			this.amc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void dossuranceManagementDialog() {
		this.amc.displayDialog();
	}
	
	

	
	public Prelevement modifierPrelevement(Prelevement p) {
		PrelevementEditorPane cep = new PrelevementEditorPane(this.primaryStage, this.dbs);
		Prelevement result = cep.doPrelevementEditorDialog(p, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessPrelevementAutomatique ap = new AccessPrelevementAutomatique();
				ap.updatePrelevement(result);
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

	
	public Prelevement nouveauPrelevement() {
		Prelevement prelevement;
		PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dbs);
		prelevement = pep.doPrelevementEditorDialog(null, EditionMode.CREATION);
		if (prelevement != null) {
			try {
				AccessPrelevementAutomatique ap = new AccessPrelevementAutomatique();

				ap.insertPrelevement(prelevement);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				prelevement = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				prelevement = null;
			}
		}
		return prelevement;
	}

	

	
	public ArrayList<Prelevement> getlistePrelevements(int _numCompte, String debutMontant, String debutDate) {
		ArrayList<Prelevement> listePrel = new ArrayList<>();
		try {
			

			AccessPrelevementAutomatique ap = new AccessPrelevementAutomatique();
			listePrel = ap.getPrelevements(this.dbs.getEmpAct().idAg, _numCompte, debutMontant, debutDate);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listePrel = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listePrel = new ArrayList<>();
		}
		return listePrel;
	}
	
	
	public void supprimerPrelevement(Prelevement p) {		
			try {
				AccessPrelevementAutomatique ap = new AccessPrelevementAutomatique();
				ap.deletePrelevement(p);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
			}
		
		
	}

	
}
