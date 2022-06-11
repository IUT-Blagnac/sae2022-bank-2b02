package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ClientsManagement;
import application.control.PrelevementsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.Prelevement;

public class PrelevementsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private PrelevementsManagement pm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Prelevement> olp;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, PrelevementsManagement _pm, DailyBankState _dbstate) {
		this.pm = _pm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	/*
	 * Permet de configurer la fen�tre
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olp = FXCollections.observableArrayList();
		this.lvPrelevements.setItems(this.olp);
		this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelevements.getFocusModel().focus(-1);
		this.lvPrelevements.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

	/*
	 * Permet d'afficher la boite de dialogue
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtMontant;
	@FXML
	private TextField txtDate;
	@FXML
	private ListView<Prelevement> lvPrelevements;
	@FXML
	private Button btnDesactPrelevement;
	@FXML
	private Button btnModifPrelevement;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/*
	 * Permet de fermer la fen�tre 
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/*
	 * Permet de rechercher un prélèvement 
	 */
	@FXML
	private void doRechercher() {
		int numCompte;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					this.txtNum.setText("");
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			numCompte = -1;
		}

		String debutMontant = this.txtMontant.getText();
		String debutDate = this.txtDate.getText();

		if (numCompte != -1) {
			this.txtMontant.setText("");
			this.txtDate.setText("");
		} else {
			if (debutMontant.equals("") && !debutDate.equals("")) {
				this.txtDate.setText("");
			}
		}

		// Recherche des clients en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les clients
		ArrayList<Prelevement> listePrel;

		this.olp.clear();
		for (Prelevement prel : listePrel) {
			this.olp.add(prel);
		}

		this.validateComponentState();
	}

	

	/*
	 * Permet de modifier les informations li�es � un client
	 */
	@FXML
	private void doModifierPrelevement() {

		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement prelMod = this.olp.get(selectedIndice);
			Prelevement result = this.pm.modifierClient(prelMod);
			if (result != null) {
				this.olp.set(selectedIndice, result);
			}
		}
	}

	/*
	 * Permet de d�sactiver un client
	 */
	@FXML
	private void doDesactiverPrelevement() {
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement prelSup = this.olp.get(selectedIndice);
			this.pm.supprimerClient(prelSup);
			
		}
	}

	/*
	 * Permet de cr�er un nouveau client
	 */
	@FXML
	private void doNouveauPrelevement() {
		Prelevement prelevement;
		prelevement = this.pm.nouveauClient();
		if (prelevement != null) {
			this.olp.add(prelevement);
		}
	}

	/*
	 * Permet d'activer les boutons du premier client de la liste et de d�sactiver ceux des autres
	 */
	private void validateComponentState() {
		// Non implémenté => désactivé
		
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifPrelevement.setDisable(false);
			this.btnDesactPrelevement.setDisable(false);
		} else {
			this.btnModifPrelevement.setDisable(true);
			this.btnDesactPrelevement.setDisable(true);
		}
	}
}
