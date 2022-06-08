package application.view;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;

public class OperationEditorPaneController2 implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation operationResultat;

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
	 * Permet de boite de dialogue des op�rations pouvant �tre effectu�es sur un compte
	 */
	public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;

		switch (mode) {
		
		case VIREMENT:
			String info2 = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info2);

			this.btnOk.setText("Effectuer Virement");
			this.btnCancel.setText("Annuler Virement");

			ObservableList<String> list2 = FXCollections.observableArrayList();

			for (String tyOp2 : ConstantesIHM.OPERATIONS_VIREMENT_GUICHET) {
				list2.add(tyOp2);
			}

			this.cbTypeOpe.setItems(list2);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}

		this.operationResultat = null;
		this.cbTypeOpe.requestFocus();

		this.primaryStage.showAndWait();
		return this.operationResultat;
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
	private Label lblMontant;
	@FXML
	private ComboBox<String> cbTypeOpe;
	
	@FXML
	private TextField txtCompte;
	
	@FXML
	private TextField txtMontant;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public int getNumCompte() {
		return Integer.valueOf(this.txtCompte.getText().trim());
	}

	/*
	 * Permet de fermer la fen�tre d'�dition des op�rations
	 */
	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}

	/*
	 * Permet d'ajouter une op�ration dans la liste
	 */
	@FXML
	private void doAjouter() {
		switch (this.categorieOperation) {
		
		case VIREMENT:
						double montant2;
						
	
						this.txtMontant.getStyleClass().remove("borderred");
						this.lblMontant.getStyleClass().remove("borderred");
						this.lblMessage.getStyleClass().remove("borderred");
						String info2 = "Cpt. : " + this.compteEdite.idNumCompte + "  "
								+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde);
								
						this.lblMessage.setText(info2);
	
						try {
							montant2 = Double.parseDouble(this.txtMontant.getText().trim());
							
							if (montant2 <= 0)
								throw new NumberFormatException();
						} catch (NumberFormatException nfe) {
							this.txtMontant.getStyleClass().add("borderred");
							this.lblMontant.getStyleClass().add("borderred");
							this.txtMontant.requestFocus();
							return;
						}
						
						String typeOp2 = this.cbTypeOpe.getValue();
						this.operationResultat = new Operation(-1, montant2, null, null, this.compteEdite.idNumCli, typeOp2);
						this.primaryStage.close();
						break;
		}
	}
}
