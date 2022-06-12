package model.data;

public class AssuranceEmprunt {

	public int idAss, tauxAss, tauxCouv, idEmprunt;
	

	public AssuranceEmprunt(int idAss, int tauxAss, int tauxCouv , int idEmprunt) {
		super();
		this.idAss = idAss;
		this.tauxAss = tauxAss;
		this.tauxCouv = tauxCouv;
		this.idEmprunt = idEmprunt;
		
	}

	public AssuranceEmprunt(AssuranceEmprunt c) {
		this(c.idAss, c.tauxAss, c.tauxCouv, c.idEmprunt);
	}

	public AssuranceEmprunt() {
		this(-1000, -1000, -1000, -1000);
	}

	@Override
	public String toString() {
		return "[" + this.idAss + "]  " + this.tauxAss + " " + this.tauxCouv + "(" + this.idEmprunt + ")  {"
				+ "}";
	}

}
