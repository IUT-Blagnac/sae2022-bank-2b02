package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class EmployeEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Employe employeEdite;
	private EditionMode em;
	private Employe employeResult;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	/*
	 * Permet de configurer la fen�tre
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/*
	 * Permet d'�diter les informations li�es � un client
	 * @param client : le client
	 * @param mode : le mode �diteur
	 */
	public Employe displayDialog(Employe employeMod, EditionMode mode) {

		this.em = mode;
		if (employeMod == null) {
			this.employeEdite = new Employe(0, "", "", "", "", "", this.dbs.getEmpAct().idAg);
		} else {
			this.employeEdite = new Employe(employeMod);
		}
		this.employeResult = null;
		switch (mode) {
		case CREATION:
			this.txtIdEmploye.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtMotPasse.setDisable(false);
			this.txtLogin.setDisable(false);
			this.rbChefAg.setSelected(true);
			this.rbGuichetier.setSelected(false);
			
			if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
				this.rbChefAg.setDisable(false);
				this.rbGuichetier.setDisable(false);
			} else {
				this.rbChefAg.setDisable(true);
				this.rbGuichetier.setDisable(true);
			}
			
			this.lblMessage.setText("Informations sur le nouvel employé");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.txtIdEmploye.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtMotPasse.setDisable(false);
			this.txtLogin.setDisable(false);
			this.rbChefAg.setSelected(true);
			this.rbGuichetier.setSelected(false);
			
			if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
				this.rbChefAg.setDisable(false);
				this.rbGuichetier.setDisable(false);
			} else {
				this.rbChefAg.setDisable(true);
				this.rbGuichetier.setDisable(true);
			}
			
			this.lblMessage.setText("Informations employé");
			this.butOk.setText("Modifier");
			this.butCancel.setText("Annuler");
			break;
		case SUPPRESSION:
			// ce mode n'est pas utilisé pour les Clients :
			// la suppression d'un client n'existe pas il faut que le chef d'agence
			// bascule son état "Actif" à "Inactif"
			ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION CLIENT NON PREVUE",
					null);
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();

			break;
		}
		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}
		// initialisation du contenu des champs
		this.txtIdEmploye.setText("" + this.employeEdite.idEmploye);
		this.txtNom.setText(this.employeEdite.nom);
		this.txtPrenom.setText(this.employeEdite.prenom);
		this.txtLogin.setText(this.employeEdite.login);
		this.txtMotPasse.setText(this.employeEdite.motPasse);

		if (ConstantesIHM.isAdmin(this.employeEdite)) {
			this.rbChefAg.setSelected(true);
			this.rbGuichetier.setSelected(false);
		} else {
			this.rbChefAg.setSelected(false);
			this.rbGuichetier.setSelected(true);
		}
		
		this.employeResult = null;

		this.primaryStage.showAndWait();
		return this.employeResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdEmploye;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtMotPasse;
	@FXML
	private TextField txtLogin;
	@FXML
	private RadioButton rbChefAg;
	@FXML
	private RadioButton rbGuichetier;
	@FXML
	private ToggleGroup chefAgGuichetier;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.employeResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.employeResult = this.employeEdite;
			this.primaryStage.close();
			break;
		}

	}

	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.motPasse = this.txtMotPasse.getText().trim();
		this.employeEdite.login = this.txtLogin.getText().trim();
		
		if (this.rbChefAg.isSelected()) {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_CHEF;
		} else {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_GUICHETIER;
		}

		if (this.employeEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.employeEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}

		if (this.employeEdite.login.length() < 2) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le Login n'est pas valable",
					AlertType.WARNING);
			this.txtLogin.requestFocus();
			return false;
		}
		
		if (this.employeEdite.motPasse.length() < 5) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mot de passe n'est pas valable (au moins 5 caract�res)",
					AlertType.WARNING);
			this.txtMotPasse.requestFocus();
			return false;
		}

		return true;
	}
}
