package application.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankState;
import application.control.OperationsManagement;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

public class OperationsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private OperationsManagement om;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Operation> olOperation;

	private ComptesManagementController cmc;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, OperationsManagement _om, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.om = _om;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();
	}

	/*
	 * Permet de configurer la fen�tre
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olOperation = FXCollections.observableArrayList();
		this.lvOperations.setItems(this.olOperation);
		this.lvOperations.setSelectionModel(new NoSelectionModel<Operation>());
		this.updateInfoCompteClient();
		this.validateComponentState();
	}

	/*
	 * Permet d'afficher une boite de dialogue
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	public ComptesManagementController getCompteManagementController() {
		return this.cmc;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblInfosClient;
	@FXML
	private Label lblInfosCompte;
	@FXML
	private ListView<Operation> lvOperations;
	@FXML
	private Button btnDebit;
	@FXML
	private Button btnCredit;
	@FXML
	private Button btnVirement;
	@FXML
	private Button btnGenererPdf;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/*
	 * Permt de fermer la fen�tre de management des op�rations
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/*
	 * Permet d'enregistrer un d�bit
	 */
	@FXML
	private void doDebit() {

		Operation op = this.om.enregistrerDebit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	/*
	 * Permet d'enregistrer un cr�dit
	 */
	@FXML
	private void doCredit() {

		Operation op = this.om.enregistrerCredit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}


	/*
	 * Permet d'enregistrer une autre op�ration
	 */
	@FXML
	private void doAutre() {

		Operation op = this.om.enregistrerVirement();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	@FXML
	private void doGenererPDF() throws DocumentException, MalformedURLException, IOException {

		Document document = new Document (PageSize.A4 , 50, 50, 50 , 50);
		try {
			PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream("ReleveCompte"+this.clientDuCompte.nom+this.compteConcerne.idNumCompte+".pdf"));
			pdfWriter.setViewerPreferences(PdfWriter.PageLayoutTwoColumnLeft);
			document.open();
			document.add(new Chunk("")); 
			Paragraph p1 = new Paragraph("Relevé de comptes de" + " " + this.clientDuCompte.nom + " " + this.clientDuCompte.prenom + " : \n\n");
			document.add(p1);
			int taille = this.lvOperations.getItems().size();
			Paragraph p;
			for (int i = 0;i<taille;i++) {
				p = new Paragraph (this.lvOperations.getItems().get(i).toString());
				document.add(p);
			}
			p = new Paragraph("\nSolde du compte : " + this.compteConcerne.solde);
			document.add(p);
			document.close();
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void validateComponentState() {
		this.btnCredit.setDisable(false);
		this.btnDebit.setDisable(false);
		this.btnVirement.setDisable(false);
		this.btnGenererPdf.setDisable(false);
	}

	private void updateInfoCompteClient() {

		PairsOfValue<CompteCourant, ArrayList<Operation>> opesEtCompte;

		opesEtCompte = this.om.operationsEtSoldeDunCompte();

		ArrayList<Operation> listeOP;
		this.compteConcerne = opesEtCompte.getLeft();
		listeOP = opesEtCompte.getRight();

		String info;
		info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli
				+ ")";
		this.lblInfosClient.setText(info);

		info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
		this.lblInfosCompte.setText(info);

		this.olOperation.clear();
		for (Operation op : listeOP) {
			this.olOperation.add(op);
		}

		this.validateComponentState();
	}
}
