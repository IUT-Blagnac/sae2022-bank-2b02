package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.itextpdf.text.pdf.PdfWriter;

import model.data.Client;
import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

public class AccessPrelevement {

	public AccessPrelevement() {
	}

	
	public ArrayList<Prelevement> getPrelevements(int idNumCompte, int idPrelev, String debutMontant, String debutDate)
			throws DataAccessException, DatabaseConnexionException {
		ArrayList<Prelevement> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();

			PreparedStatement pst;

			String query;
			if (idPrelev != -1) {
				query = "SELECT * FROM PrelevementAutomatique where idNumCompte = ?";
				query += " AND idPrelev = ?";
				query += " ORDER BY montant";
				pst = con.prepareStatement(query);
				pst.setInt(1, idNumCompte);
				pst.setInt(2, idPrelev);

			} else if (!debutMontant.equals("")) {
				debutMontant = debutMontant.toUpperCase() + "%";
				debutDate = debutDate.toUpperCase() + "%";
				query = "SELECT * FROM PrelevementAutomatique where idNumCompte = ?";
				query += " AND UPPER(montant) like ?" + " AND UPPER(datecurrente) like ?";
				query += " ORDER BY montant";
				pst = con.prepareStatement(query);
				pst.setInt(1, idNumCompte);
				pst.setString(2, debutMontant);
				pst.setString(3, debutDate);
			} else {
				query = "SELECT * FROM PrelevementAutomatique where idAg = ?";
				query += " ORDER BY nom";
				pst = con.prepareStatement(query);
				pst.setInt(1, idAg);
			}
			System.err.println(query + " nom : " + debutNom + " prenom : " + debutPrenom + "#");

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idNumCliTR = rs.getInt("idNumCli");
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				String adressePostale = rs.getString("adressePostale");
				adressePostale = (adressePostale == null ? "" : adressePostale);
				String email = rs.getString("email");
				email = (email == null ? "" : email);
				String telephone = rs.getString("telephone");
				telephone = (telephone == null ? "" : telephone);
				String estInactif = rs.getString("estInactif");
				int idAgCli = rs.getInt("idAg");

				alResult.add(
						new Client(idNumCliTR, nom, prenom, adressePostale, email, telephone, estInactif, idAgCli));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.SELECT, "Erreur accÃ¨s", e);
		}

		return alResult;
	}

	/**
	 * Recherche de client par son id.
	 *
	 * @return un Client ou null si non trouvÃ©
	 * @param idCli id du client recherchÃ© (clÃ© primaire)
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public Client getPrelevement(int idCli)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		Client clientTrouve;

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM Client where" + " idNumCli = ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idCli);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				// TrouvÃ© ...
				int idNumCli = rs.getInt("idNumCli");
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				String adressePostale = rs.getString("adressePostale");
				adressePostale = (adressePostale == null ? "" : adressePostale);
				String email = rs.getString("email");
				email = (email == null ? "" : email);
				String telephone = rs.getString("telephone");
				telephone = (telephone == null ? "" : telephone);
				String estInactif = rs.getString("estInactif");
				int idAgCli = rs.getInt("idAg");

				clientTrouve = new Client(idNumCli, nom, prenom, adressePostale, email, telephone, estInactif, idAgCli);
			} else {
				// Non trouvÃ© ...
				rs.close();
				pst.close();
				return null;
			}

			if (rs.next()) {
				// Plus de 2 ? Bizarre ...
				rs.close();
				pst.close();
				throw new RowNotFoundOrTooManyRowsException(Table.Client, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return clientTrouve;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.SELECT, "Erreur accÃ¨s", e);
		}
	}

	/**
	 * Insertion d'un client.
	 *
	 * @param client IN/OUT Tous les attributs IN sauf idNumCli en OUT
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void insertPrelevement(Prelevement prelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {

			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO PRELEVEMENTAUTOMATIQUE VALUES (" + "seq_id_client.NEXTVAL" + ", " + "?" + ", " + "?" + ", "
					+ "?" + ", " + "?" + ", " + "?" + ", " + "?" + ", " + "?" + ")";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, client.nom);
			pst.setString(2, client.prenom);
			pst.setString(3, client.adressePostale);
			pst.setString(4, client.email);
			pst.setString(5, client.telephone);
			pst.setString(6, "" + client.estInactif.charAt(0));
			pst.setInt(7, client.idAg);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Client, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_client.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numCliBase = rs.getInt(1);

			con.commit();
			rs.close();
			pst2.close();

			client.idNumCli = numCliBase;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.INSERT, "Erreur accÃ¨s", e);
		}
	}

	
	public void updatePrelevement(Prelevement prelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE PRELEVEMENTAUTOMATIQUE SET " + "montant = " + "? , " + "datecurrente = " + "? , " + "beneficiaire = "
					+ "? , "  + " "
					+ "WHERE idPrelev = ? ";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, prelevement.montant);
			pst.setString(2, prelevement.dateRecurrente);
			pst.setString(3, prelevement.beneficiaire);
			pst.setInt(4, prelevement.idPrelev);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.UPDATE, "Erreur accÃ¨s", e);
		}
	}
	
	/*
	 * Clôturer tous les comptes d'un client
	 * 
	 * @param client IN client.idNumCli (clÃ© primaire) doit exister
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void deleteClient(Prelevement prelevement)
			throws DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE CompteCourant SET estCloture = 'O' " + "WHERE idNumCli = ? ";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, client.idNumCli);

			System.err.println(query);
			
			
			int result = pst.executeUpdate();
			pst.close();
			
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.UPDATE, "Erreur accÃ¨s", e);
		}
	}
}
