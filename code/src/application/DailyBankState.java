package application;

import application.tools.ConstantesIHM;
import model.data.AgenceBancaire;
import model.data.Employe;

public class DailyBankState {
	private Employe empAct;
	private AgenceBancaire agAct;
	private boolean isChefDAgence;

	/**
	*Renvoie l'employé actif
	*@return les informations liées à l'employé actif
	**/
	public Employe getEmpAct() {
		return this.empAct;
	}

	/**
	*Définit un employé actif
	**/
	public void setEmpAct(Employe employeActif) {
		this.empAct = employeActif;
	}

	/**
	*Renvoie l'agence bancaire active
	*@return les informations liées à l'agence bancaire active
	**/
	public AgenceBancaire getAgAct() {
		return this.agAct;
	}

	/**
	*Définit une nouvelle agence bancaire active
	**/
	public void setAgAct(AgenceBancaire agenceActive) {
		this.agAct = agenceActive;
	}

	/**
	*Permet de connaitre le chef de l'agence bancaire
	*@return true si la personne est chef d'agence, false sinon
	**/
	public boolean isChefDAgence() {
		return this.isChefDAgence;
	}

	/**
	*Définit un nouveau chef d'agence
	*@param isChefDAgence : rôle du chef d'agence
	**/
	public void setChefDAgence(boolean isChefDAgence) {
		this.isChefDAgence = isChefDAgence;
	}

	/**
	*Ajoute les droits d'accès au chf d'agence
	*@param droitAccess : droit d'accès du chef d'agence
	**/
	public void setChefDAgence(String droitsAccess) {
		this.isChefDAgence = false;

		if (droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			this.isChefDAgence = true;
		}
	}
}
