package model.data;

public class Prelevement {

	public int idPrelev, idNumCompte, montant, dateRecurrente;
	public String  beneficiaire;


	public Prelevement(int idPrelev, int montant2, int dateRecurrente, String beneficiaire, int idNumCompte) {
		super();
		this.idPrelev = idPrelev;
		this.montant = montant2;
		this.dateRecurrente = dateRecurrente;
		this.beneficiaire = beneficiaire;
		this.idNumCompte = idNumCompte;
	}

	public Prelevement(Prelevement e) {
		this(e.idPrelev, e.montant, e.dateRecurrente, e.beneficiaire, e.idNumCompte);
	}

	public Prelevement() {
		this(-1000, -1000, -1000, null, -1000);
	}

	@Override
	public String toString() {
		return "Employe [idPrelev=" + this.idPrelev + ", montant=" + this.montant + ", dateRecurrente=" + this.dateRecurrente
				+ ", beneficiaire=" + this.beneficiaire + ", idNumCompte=" + this.idNumCompte + "]";
	}

}
