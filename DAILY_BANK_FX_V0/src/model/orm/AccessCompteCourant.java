package model.orm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.Client;
import model.data.CompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

public class AccessCompteCourant {

	public AccessCompteCourant() {
	}

	/**
	 * Recherche des CompteCourant d'un client à partir de son id.
	 *
	 * @param idNumCli id du client dont on cherche les comptes
	 * @return Tous les CompteCourant de idNumCli (ou liste vide)
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public ArrayList<CompteCourant> getCompteCourants(int idNumCli)
			throws DataAccessException, DatabaseConnexionException {
		ArrayList<CompteCourant> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM CompteCourant where idNumCli = ?";
			query += " ORDER BY idNumCompte";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCli);
			System.err.println(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idNumCompte = rs.getInt("idNumCompte");
				int debitAutorise = rs.getInt("debitAutorise");
				double solde = rs.getDouble("solde");
				String estCloture = rs.getString("estCloture");
				int idNumCliTROUVE = rs.getInt("idNumCli");

				alResult.add(new CompteCourant(idNumCompte, debitAutorise, solde, estCloture, idNumCliTROUVE));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}

		return alResult;
	}

	/**
	 * Recherche d'un CompteCourant à partir de son id (idNumCompte).
	 *
	 * @param idNumCompte id du compte (clé primaire)
	 * @return Le compte ou null si non trouvé
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public CompteCourant getCompteCourant(int idNumCompte)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			CompteCourant cc;

			Connection con = LogToDatabase.getConnexion();

			String query = "SELECT * FROM CompteCourant where" + " idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			System.err.println(query);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int idNumCompteTROUVE = rs.getInt("idNumCompte");
				int debitAutorise = rs.getInt("debitAutorise");
				double solde = rs.getDouble("solde");
				String estCloture = rs.getString("estCloture");
				int idNumCliTROUVE = rs.getInt("idNumCli");

				cc = new CompteCourant(idNumCompteTROUVE, debitAutorise, solde, estCloture, idNumCliTROUVE);
			} else {
				rs.close();
				pst.close();
				return null;
			}

			if (rs.next()) {
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return cc;
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * Mise à jour d'un CompteCourant.
	 *
	 * cc.idNumCompte (clé primaire) doit exister seul cc.debitAutorise est mis à
	 * jour cc.solde non mis à jour (ne peut se faire que par une opération)
	 * cc.idNumCli non mis à jour (un cc ne change pas de client)
	 *
	 * @param cc IN cc.idNumCompte (clé primaire) doit exister seul
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 * @throws ManagementRuleViolation
	 */
	public void updateCompteCourant(CompteCourant cc) throws RowNotFoundOrTooManyRowsException, DataAccessException,
			DatabaseConnexionException, ManagementRuleViolation {
		try {

			CompteCourant cAvant = this.getCompteCourant(cc.idNumCompte);
			if (cc.debitAutorise > 0) {
				cc.debitAutorise = -cc.debitAutorise;
			}
			if (cAvant.solde < cc.debitAutorise) {
				throw new ManagementRuleViolation(Table.CompteCourant, Order.UPDATE,
						"Erreur de règle de gestion : sole à découvert", null);
			}
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE CompteCourant SET " + "debitAutorise = ? " + "WHERE idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, cc.debitAutorise);
			pst.setInt(2, cc.idNumCompte);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.UPDATE, "Erreur accès", e);
		}
	}
	
	/**
	 * Insertion d'un client.
	 *
	 * @param client IN/OUT Tous les attributs IN sauf idNumCli en OUT
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 * @throws ManagementRuleViolation 
	 */
	public void insertCompte(CompteCourant cc)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException, ManagementRuleViolation {
		try {
			Connection con = LogToDatabase.getConnexion();
			CallableStatement call;

			String q = "{call CreerCompte (?, ?, ?, ?)}";
			// les ? correspondent aux paramètres : cf. déf procédure (4 paramètres)
			call = con.prepareCall(q);
			// Paramètres in
			cc.debitAutorise = - cc.debitAutorise;
			call.setInt(1, cc.debitAutorise);
			// 1 -> valeur du premier paramètre, cf. déf procédure
			call.setDouble(2, cc.solde);
			call.setInt(3, cc.idNumCli);
			// Paramètres out
			call.registerOutParameter(4, java.sql.Types.INTEGER);
			// 4 type du quatrième paramètre qui est déclaré en OUT, cf. déf procédure

			call.execute();

			int res = call.getInt(4);

			if (res == -1) { // Erreur applicative
				throw new ManagementRuleViolation(Table.Operation, Order.INSERT,
						"Erreur de compte: le solde doit être superieur à 50", null);
			}
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.INSERT, "Erreur accès", e);
		}
	}
	
	public void deleteCompte(CompteCourant cc)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException, ManagementRuleViolation {
			try {

				Connection con = LogToDatabase.getConnexion();
				System.out.println(3);
				String query = "UPDATE CompteCourant SET estCloture = 'O' WHERE idNumCompte = ?";
				PreparedStatement pst = con.prepareStatement(query);
				pst.setInt(1, cc.idNumCompte);

				System.err.println(query);

				int result = pst.executeUpdate();
				pst.close();

				if (result != 1) {
					con.rollback();
					throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.DELETE,
							"Erreur lors de la cloturation du compte", null, result);
				}

				con.commit();

			} catch (SQLException e) {
				throw new DataAccessException(Table.CompteCourant, Order.DELETE, "Erreur accès", e);
			}
	}
}
