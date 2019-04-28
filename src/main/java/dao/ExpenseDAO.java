package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Expense;

/**
 * 社員データを扱うDAO
 */
public class ExpenseDAO {
	/**
	 * クエリ文字列
	 */

	/**着手済・着手中**/
	private static final String SELECT_ALL_QUERY = "SELECT EXP.ID ,EXP.REPORT_DATE ,EXP.UPDATE_DATE ,EXP.EMPID ,EMP1.NAME ,EXP.TITLE ,EXP.MONEY , "
							+"EXP.STATUS_ID ,STA.STATUS ,EXP.PLACE ,EXP.UPDATE_EMPID ,EMP2.NAME UPDATE_NAME ,EXP.TEXT  "
							+"FROM EXPENSE2 EXP ,EMPLOYEE EMP1 ,EMPLOYEE EMP2 ,EXP_STATUS STA "
							+"WHERE 1=1 AND EXP.EMPID = EMP1.EMPID AND EXP.STATUS_ID = STA.ID AND EXP.UPDATE_EMPID = EMP2.EMPID(+) ";

	private static final String INSERT_QUERY = "INSERT INTO "
			+"EXPENSE2(ID, REPORT_DATE, UPDATE_DATE, EMPID, TITLE, MONEY, STATUS_ID, PLACE, UPDATE_EMPID, TEXT) "
			+"VALUES(?,?,?,?,?,?,?,?,?,?)";


	/**未着手**/
	private static final String SELECT_BY_ID_QUERY = SELECT_ALL_QUERY + " WHERE EMP.ID = ?";

	private static final String UPDATE_QUERY = "UPDATE EMPLOYEE "
							+"SET EMPID=?,NAME=?,AGE=?,GENDER=?,PHOTOID=?,ZIP=?,PREF=?,"
							+"ADDRESS=?,POSTID=?,ENTDATE=?,RETDATE=? WHERE ID = ?";
	private static final String DELETE_QUERY = "DELETE FROM EMPLOYEE WHERE ID = ?";



	/**着手済・着手中**/

	/**
	 * 検索結果からオブジェクトを復元する。
	 *
	 * @param rs 検索結果が収められているResultSet。rs.next()がtrueであることが前提。
	 * @return 検索結果を収めたオブジェクト
	 * @throws SQLException 検索結果取得中に何らかの問題が発生した場合に送出される。
	 */
	private Expense processRow(ResultSet rs) throws SQLException {
		Expense result = new Expense();

		// Expense本体の再現
		result.setId(rs.getInt("ID"));
		Date repDate = rs.getDate("REPORT_DATE");
		if (repDate != null) {
			result.setReportDate(repDate.toString());
		}
		Date updDate = rs.getDate("UPDATE_DATE");
		if (updDate != null) {
			result.setUpdateDate(updDate.toString());
		}
		result.setEmpId(rs.getString("EMPID"));
		result.setName(rs.getString("NAME"));
		result.setTitle(rs.getString("TITLE"));
		result.setMoney(rs.getInt("MONEY"));
		result.setStatusId(rs.getInt("STATUS_ID"));
		result.setStatus(rs.getString("STATUS"));
		result.setPlace(rs.getString("PLACE"));
		result.setUpdateEmpId(rs.getString("UPDATE_EMPID"));
		result.setUpdateName(rs.getString("UPDATE_NAME"));
		result.setReason(rs.getString("TEXT"));

		return result;
	}


	/**
	 * パラメータ指定の検索を実施する。
	 * 有効なパラメータ指定が1つも存在しない場合は全件検索になる。
	 *
	 * @param param 検索用のパラメータを収めたオブジェクト。
	 * @return 検索結果を収めたList。検索結果が存在しない場合は長さ0のリストが返る。
	 */
	public List<Expense> findByParam(ExpParam param) {
		List<Expense> result = new ArrayList<>();

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		String queryString = SELECT_ALL_QUERY + param.getWhereClause();
		try (PreparedStatement statement = connection.prepareStatement(queryString)) {
			param.setParameter(statement);

			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				result.add(processRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return result;
	}


	/**
	 * オブジェクトからSQLにパラメータを展開する。
	 *
	 * @param statement パラメータ展開対象のSQL
	 * @param expense パラメータに対して実際の値を供給するオブジェクト
	 * @param forUpdate 更新に使われるならtrueを、新規追加に使われるならfalseを指定する。
	 * @throws SQLException パラメータ展開時に何らかの問題が発生した場合に送出される。
	 */
	private void setParameter(PreparedStatement statement, Expense expense, boolean forUpdate) throws SQLException {
		int count = 1;

		statement.setInt(count++, expense.getId());
		if (expense.getReportDate() != null) {
			statement.setDate(count++, Date.valueOf(expense.getReportDate()));
		} else {
			statement.setDate(count++, null);
		}
		if (expense.getUpdateDate() != null) {
			statement.setDate(count++, Date.valueOf(expense.getUpdateDate()));
		} else {
			statement.setDate(count++, null);
		}
		statement.setString(count++, expense.getEmpId());
//		statement.setString(count++, expense.getName());
		statement.setString(count++, expense.getTitle());
		statement.setInt(count++, expense.getMoney());
		statement.setInt(count++, expense.getStatusId());
//		statement.setString(count++, expense.getStatus());
		statement.setString(count++, expense.getPlace());
		statement.setString(count++, expense.getUpdateEmpId());
//		statement.setString(count++, expense.getUpdateName());
		statement.setString(count++, expense.getReason());


		if (forUpdate) {
			statement.setInt(count++, expense.getId());
		}
	}


	/**
	 * 指定されたExpenseオブジェクトを新規にDBに登録する。
	 * 登録されたオブジェクトにはDB上のIDが上書きされる。
	 * 何らかの理由で登録に失敗した場合、IDがセットされない状態（=0）で返却される。
	 *
	 * @param Expense 登録対象オブジェクト
	 * @return DB上のIDがセットされたオブジェクト
	 */

	public Expense create(Expense expense) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return expense;
		}

		try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[] { "ID" });) {
			// INSERT実行
			setParameter(statement, expense, false);
			statement.executeUpdate();

			// INSERTできたらKEYを取得
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);
			expense.setId(id);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return expense;
	}





	/**以降未着手**/




//	/**
//	 * ID指定の検索を実施する。
//	 *
//	 * @param id 検索対象のID
//	 * @return 検索できた場合は検索結果データを収めたPostインスタンス。検索に失敗した場合はnullが返る。
//	 */
//	public Employee findById(int id) {
//		Employee result = null;
//
//		Connection connection = ConnectionProvider.getConnection();
//		if (connection == null) {
//			return result;
//		}
//
//		try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
//			statement.setInt(1, id);
//
//			ResultSet rs = statement.executeQuery();
//
//			if (rs.next()) {
////				result = processRow(rs);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			ConnectionProvider.close(connection);
//		}
//
//		return result;
//	}
//
//
//

//	/**
//	 * 指定されたEmployeeオブジェクトを使ってDBを更新する。
//	 *
//	 * @param employee 更新対象オブジェクト
//	 * @return 更新に成功したらtrue、失敗したらfalse
//	 */
//	public Employee update(Employee employee) {
//		Connection connection = ConnectionProvider.getConnection();
//		if (connection == null) {
//			return employee;
//		}
//
//		try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
//			setParameter(statement, employee, true);
//			statement.executeUpdate();
//		} catch (SQLException ex) {
//			ex.printStackTrace();
//		} finally {
//			ConnectionProvider.close(connection);
//		}
//
//		return employee;
//	}
//
//	/**
//	 * 指定されたIDのPostデータを削除する。
//	 *
//	 * @param id 削除対象のPostデータのID
//	 * @return 削除が成功したらtrue、失敗したらfalse
//	 */
//	public boolean remove(int id) {
//		Connection connection = ConnectionProvider.getConnection();
//		if (connection == null) {
//			return false;
//		}
//
//		int count = 0;
//		try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
//			// DELETE実行
//			statement.setInt(1, id);
//			count = statement.executeUpdate();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			ConnectionProvider.close(connection);
//		}
//		return count == 1;
//	}


}
