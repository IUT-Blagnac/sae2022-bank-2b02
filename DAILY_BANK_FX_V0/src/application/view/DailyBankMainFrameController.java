package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.DailyBankMainFrame;
import application.tools.AlertUtilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.AgenceBancaire;
import model.data.Employe;

public class DailyBankMainFrameController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private DailyBankMainFrame dbmf;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, DailyBankMainFrame _dbmf, DailyBankState _dbstate) {
		this.dbmf = _dbmf;
		this.dbs = _dbstate;
		this.primaryStage = _containingStage;
		this.configure();
		this.validateComponentState();
	}

	/*
	 * Permet d'afficher une boite de dialogue
	 */
	public void displayDialog() {
		this.primaryStage.show();
	}

	/*
	 * Permet de configurer la fen�tre
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.btnConn.managedProperty().bind(this.btnConn.visibleProperty());
		this.btnDeconn.managedProperty().bind(this.btnDeconn.visibleProperty());
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doQuit();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblAg;
	@FXML
	private Label lblAdrAg;
	@FXML
	private Label lblEmpNom;
	@FXML
	private Label lblEmpPrenom;
	@FXML
	private MenuItem mitemClient;
	@FXML
	private MenuItem mitemEmploye;
	@FXML
	private MenuItem mitemConnexion;
	@FXML
	private MenuItem mitemDeConnexion;
	@FXML
	private MenuItem mitemQuitter;
	@FXML
	private Button btnConn;
	@FXML
	private Button btnDeconn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/*
	 * Permet de quitter l'application principale en demandant une confirmation avant de fermet la fen�tre
	 */
	@FXML
	private void doQuit() {

		this.actionQuitterBD();

		if (AlertUtilities.confirmYesCancel(this.primaryStage, "Quitter Appli Principale",
				"Etes vous sur de vouloir quitter l'appli ?", null, AlertType.CONFIRMATION)) {
			this.primaryStage.close();
		}
	}

	/*
	 * Permet d'afficher la fen�tre d'aide
	 */
	@FXML
	private void doActionAide() {
		String contenu = "DailyBank v1.01\nSAE 2.01 Développement\nIUT-Blagnac";
		AlertUtilities.showAlert(this.primaryStage, "Aide", null, contenu, AlertType.INFORMATION);
	}

	/*
	 * Permet de v�rifier que les informations du login soient valides
	 */
	@FXML
	private void doLogin() {

		this.dbmf.login();
		this.validateComponentState();
	}

	/*
	 * Permet de se d�conncter
	 */
	@FXML
	private void doDisconnect() {
		this.dbmf.disconnect();
		this.validateComponentState();
	}

	/*
	 * Permet de v�rifier que les informations d'un compte soient valides
	 */
	private void validateComponentState() {
		Employe e = this.dbs.getEmpAct();
		AgenceBancaire a = this.dbs.getAgAct();
		if (e != null && a != null) {
			this.lblAg.setText(a.nomAg);
			this.lblAdrAg.setText(a.adressePostaleAg);
			this.lblEmpNom.setText(e.nom);
			this.lblEmpPrenom.setText(e.prenom);
			if (this.dbs.isChefDAgence()) {
				this.mitemEmploye.setDisable(false);
			} else {
				this.mitemEmploye.setDisable(true);
			}
			this.mitemClient.setDisable(false);
			this.mitemConnexion.setDisable(true);
			this.mitemDeConnexion.setDisable(false);
			this.btnConn.setVisible(false);
			this.btnDeconn.setVisible(true);
		} else {
			this.lblAg.setText("");
			this.lblAdrAg.setText("");
			this.lblEmpNom.setText("");
			this.lblEmpPrenom.setText("");

			this.mitemClient.setDisable(true);
			this.mitemEmploye.setDisable(true);
			this.mitemConnexion.setDisable(false);
			this.mitemDeConnexion.setDisable(true);
			this.btnConn.setVisible(true);
			this.btnDeconn.setVisible(false);
		}
	}

	/*
	 * Affiche la fen�tre de gestion des clients
	 */
	@FXML
	private void doClientOption() {
		this.dbmf.gestionClients();
	}
	

	/*
	 * Permet de g�rer les informations li�es aux employ�s
	 */
	@FXML
	private void doEmployeOption() {
		this.dbmf.gestionEmployees();
	}

	/*
	 * Permet de quitter la base de donn�es
	 */
	private void actionQuitterBD() {
		this.dbmf.disconnect();
	}
}
