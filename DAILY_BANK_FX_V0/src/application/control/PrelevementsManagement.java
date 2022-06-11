package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
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
import model.orm.AccessPrelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class PrelevementsManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private PrelevementsManagementController pmc;

	
	public PrelevementsManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(PrelevementsManagementController.class.getResource("prelevementsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des prélèvements automatiques");
			this.primaryStage.setResizable(false);

			this.pmc = loader.getController();
			this.pmc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Permet d'afficher la boite de dialogue de management du client 
	 */
	public void doPrelevementsManagementDialog() {
		this.pmc.displayDialog();
	}
	
	

	
	public Prelevement modifierPrelevement(Prelevement p) {
		PrelevementEditorPane cep = new PrelevementEditorPane(this.primaryStage, this.dbs);
		Prelevement result = cep.doPrelevementEditorDialog(p, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessPrelevement ap = new AccessPrelevement();
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
				AccessPrelevement ap = new AccessPrelevement();

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

	

	
	public ArrayList<Prelevement> getlistePrelevements(int _numCompte, String _debutNom, String _debutPrenom) {
		ArrayList<Prelevement> listePrel = new ArrayList<>();
		try {
			

			AccessPrelevement ap = new AccessPrelevement();
			listePrel = ap.getPrelevements(this.dbs.getEmpAct().idAg, _numCompte, _debutNom, _debutPrenom);

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
				AccessPrelevement ap = new AccessPrelevement();
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
