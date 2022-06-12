package application.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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
import model.data.Client;
import model.data.Prelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class PrelevementEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Prelevement prelevementEdite;
	private EditionMode em;
	private Prelevement prelevementResult;

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

	
	public Prelevement displayDialog(Prelevement prelevement, EditionMode mode) {

		this.em = mode;
		if (prelevement == null) {
			this.prelevementEdite = new Prelevement(0, 0, 0, "", 0);
		} else {
			this.prelevementEdite = new Prelevement(prelevement);
		}
		this.prelevementResult = null;
		switch (mode) {
		case CREATION:
			this.txtIdPrel.setDisable(true);
			this.txtMontant.setDisable(false);
			this.txtDate.setDisable(false);
			this.txtBeneficiaire.setDisable(false);
	
			this.lblMessage.setText("Informations sur le nouveau prélèvement");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.txtIdPrel.setDisable(true);
			this.txtMontant.setDisable(false);
			this.txtDate.setDisable(false);
			this.txtBeneficiaire.setDisable(false);
			this.lblMessage.setText("Informations prélèvement");
			this.butOk.setText("Modifier");
			this.butCancel.setText("Annuler");
			break;
		case SUPPRESSION:
			// ce mode n'est pas utilisé pour les Prélèvements :
			// la suppression d'un client n'existe pas il faut que le chef d'agence
			// bascule son état "Actif" à "Inactif"
			ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION PRELEVEMENT AUTOMATIQUE NON PREVUE",
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
		this.txtIdPrel.setText("" + this.prelevementEdite.idPrelev);
		this.txtMontant.setText(""+this.prelevementEdite.montant);
		this.txtDate.setText(""+this.prelevementEdite.dateRecurrente);
		this.txtBeneficiaire.setText(this.prelevementEdite.beneficiaire);
		

		

		this.prelevementResult = null;

		this.primaryStage.showAndWait();
		return this.prelevementResult;
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
	private TextField txtIdPrel;
	@FXML
	private TextField txtMontant;
	@FXML
	private TextField txtDate;
	@FXML
	private TextField txtBeneficiaire;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.prelevementResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.prelevementResult = this.prelevementEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.prelevementResult = this.prelevementEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.prelevementResult = this.prelevementEdite;
			this.primaryStage.close();
			break;
		}

	}

	private boolean isSaisieValide() {
		this.prelevementEdite.montant = Integer.valueOf(this.txtMontant.getText().trim());
		this.prelevementEdite.dateRecurrente = Integer.valueOf(this.txtDate.getText().trim());
		this.prelevementEdite.beneficiaire = this.txtBeneficiaire.getText().trim();

		if (String.valueOf(this.prelevementEdite.montant).isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le montant ne doit pas être vide",
					AlertType.WARNING);
			this.txtMontant.requestFocus();
			return false;
		}
		if (String.valueOf(this.prelevementEdite.dateRecurrente).isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La date récurrente ne doit pas être vide",
					AlertType.WARNING);
			this.txtDate.requestFocus();
			return false;
		}


		return true;
	}
}
