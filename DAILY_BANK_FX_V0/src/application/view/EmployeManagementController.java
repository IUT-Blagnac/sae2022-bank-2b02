package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.EmployeManagement;
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
import model.data.Employe;

public class EmployeManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private EmployeManagement em;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> olc;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, EmployeManagement _em, DailyBankState _dbstate) {
		this.em = _em;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	/*
	 * Permet de configurer la fen�tre
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olc = FXCollections.observableArrayList();
		this.lvEmployes.setItems(this.olc);
		this.lvEmployes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvEmployes.getFocusModel().focus(-1);
		this.lvEmployes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
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
	private TextField txtId;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Employe> lvEmployes;
	@FXML
	private Button btnSupprEmploye;
	@FXML
	private Button btnModifEmploye;
	

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
	 * Permet de rechercher un employé 
	 */
	@FXML
	private void doRechercher() {
		int numCompte;
		try {
			String nc = this.txtId.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					this.txtId.setText("");
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtId.setText("");
			numCompte = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (numCompte != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}

		// Recherche des clients en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les clients
		ArrayList<Employe> listeEmploye;
		listeEmploye = this.em.getlisteComptes(numCompte, debutNom, debutPrenom);

		this.olc.clear();
		for (Employe employe : listeEmploye) {
			this.olc.add(employe);
		}

		this.validateComponentState();
	}


	/*
	 * Permet de modifier les informations li�es � un client
	 */
	@FXML
	private void doModifierEmploye() {

		int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe employeMod = this.olc.get(selectedIndice);
			Employe result = this.em.modifierEmploye(employeMod);
			if (result != null) {
				this.olc.set(selectedIndice, result);
			}
		}
	}
	

	/*
	 * Permet de d�sactiver un client
	 */
	@FXML
	private void doSupprimerEmploye() {
		int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe employeSuppr = this.olc.get(selectedIndice);
			this.em.supprimerEmploye(employeSuppr);
		}
		doRechercher();
		
	}

	/*
	 * Permet de cr�er un nouveau client
	 */
	@FXML
	private void doNouveauEmploye() {
		Employe employe;
		employe = this.em.nouveauEmploye();
		if (employe != null) {
			this.olc.add(employe);
		}
	}

	/*
	 * Permet d'activer les boutons du premier client de la liste et de d�sactiver ceux des autres
	 */
	private void validateComponentState() {
		// Non implémenté => désactivé
		
		int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifEmploye.setDisable(false);
			this.btnSupprEmploye.setDisable(false);
		} else {
			this.btnModifEmploye.setDisable(true);
			this.btnSupprEmploye.setDisable(true);
		}
	}
}
