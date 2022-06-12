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

public class AccessPrelevementAutomatique {

	public AccessPrelevementAutomatique() {
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
				query += " AND UPPER(montant) like ?" + " AND UPPER(daterecurrente) like ?";
				query += " ORDER BY montant";
				pst = con.prepareStatement(query);
				pst.setInt(1, idNumCompte);
				pst.setString(2, debutMontant);
				pst.setString(3, debutDate);
			} else {
				query = "SELECT * FROM PrelevementAutomatique where idNumCompte = ?";
				query += " ORDER BY idPrelev";
				pst = con.prepareStatement(query);
				pst.setInt(1, idNumCompte);
			}
			System.err.println(query + " montant : " + debutMontant + " daterecurrente : " + debutDate + "#");

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idPrelevTR = rs.getInt("idPrelev"); 
				int montant = rs.getInt("montant");
				int date = rs.getInt("daterecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				beneficiaire = (beneficiaire == null ? "" : beneficiaire);
				int idNumCompte2 = rs.getInt("idNumCompte");

				alResult.add(
						new Prelevement(idPrelevTR, montant, date, beneficiaire, idNumCompte2));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accÃ¨s", e);
		}

		return alResult;
	}


	public Prelevement getPrelevement(int idPrelev)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		Prelevement prelevementTrouve;

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM PrelevementAutomatique where" + " idPrelev = ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idPrelev);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				// TrouvÃ© ...
				int idPrelev1 = rs.getInt("idPrelev");
				int montant = rs.getInt("montant");
				int date = rs.getInt("daterecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				beneficiaire = (beneficiaire == null ? "" : beneficiaire);
				int idNumCompte = rs.getInt("idNumCompte");

				prelevementTrouve = new Prelevement(idPrelev1, montant, date, beneficiaire, idNumCompte);
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
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return prelevementTrouve;
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accÃ¨s", e);
		}
	}


	public void insertPrelevement(Prelevement prelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {

			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO PRELEVEMENTAUTOMATIQUE VALUES (" + "SEQ_ID_PRELEVAUTO.NEXTVAL" + ", " + "?" + ", " + "?" + ", "
					+ "?" + ", " + "?" + ")";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, prelevement.montant);
			pst.setInt(2, prelevement.dateRecurrente);
			pst.setString(3, prelevement.beneficiaire);
			pst.setInt(4, prelevement.idNumCompte);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT SEQ_ID_PRELEVAUTO.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numPrelBase = rs.getInt(1);

			con.commit();
			rs.close();
			pst2.close();

			prelevement.idPrelev = numPrelBase;
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Erreur accÃ¨s", e);
		}
	}

	
	public void updatePrelevement(Prelevement prelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE PRELEVEMENTAUTOMATIQUE SET " + "montant = " + "? , " + "daterecurrente = " + "? , " + "beneficiaire = "  + "?"
					+ "WHERE idPrelev = ? ";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, prelevement.montant);
			pst.setInt(2, prelevement.dateRecurrente);
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
	
	
	public void deletePrelevement(Prelevement prelevement)
			throws DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE FROM PRELEVEMENTAUTOMATIQUE " + "WHERE idPrelev = ? ";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, prelevement.idPrelev);

			System.err.println(query);
			
			
			int result = pst.executeUpdate();
			pst.close();
			
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.DELETE, "Erreur accÃ¨s", e);
		}
	}
}
