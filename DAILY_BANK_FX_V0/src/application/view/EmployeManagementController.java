package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ClientsManagement;
import application.control.EmployeesManagement;
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
import model.data.Employe;

public class EmployeManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private EmployeesManagement em;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> olc;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, EmployeesManagement _em, DailyBankState _dbstate) {
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
		this.lvClients.setItems(this.olc);
		this.lvClients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvClients.getFocusModel().focus(-1);
		this.lvClients.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
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
	private ListView<Client> lvClients;
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
	private void doModifierClient() {

		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe employeMod = this.olc.get(selectedIndice);
			Employe result = this.em.modifierClient(employeMod);
			if (result != null) {
				this.olc.set(selectedIndice, result);
			}
		}
	}
	

	/*
	 * Permet de d�sactiver un client
	 */
	@FXML
	private void doDesactiverClient() {
	}

	/*
	 * Permet de cr�er un nouveau client
	 */
	@FXML
	private void doNouveauClient() {
		Client client;
		client = this.em.nouveauClient();
		if (client != null) {
			this.olc.add(client);
		}
	}

	/*
	 * Permet d'activer les boutons du premier client de la liste et de d�sactiver ceux des autres
	 */
	private void validateComponentState() {
		// Non implémenté => désactivé
		this.btnSupprEmploye.setDisable(true);
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifEmploye.setDisable(false);
		} else {
			this.btnModifEmploye.setDisable(true);
		}
	}
}
