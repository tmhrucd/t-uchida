package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Employee;
import beans.Post;

/**
 * 社員データを扱うDAO
 */
public class EmployeeDAO {
	/**
	 * クエリ文字列
	 */
	private static final String SELECT_ALL_QUERY = "SELECT EMP.ID AS ID, EMP.EMPID, EMP.NAME, EMP.AGE, EMP.GENDER, EMP.PHOTOID, EMP.ZIP, EMP.PREF, EMP.ADDRESS, "
							+"EMP.POSTID, POST.NAME as POST_NAME, EMP.ENTDATE, EMP.RETDATE "
							+"FROM EMPLOYEE EMP "
							+"INNER JOIN POST POST "
							+"ON EMP.POSTID = POST.ID";
	private static final String SELECT_BY_ID_QUERY = SELECT_ALL_QUERY + " WHERE EMP.ID = ?";
	private static final String INSERT_QUERY = "INSERT INTO "
							+"EMPLOYEE(EMPID, NAME, AGE, GENDER, PHOTOID, ZIP, PREF, ADDRESS, POSTID, ENTDATE, RETDATE) "
							+"VALUES(?,?,?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE_QUERY = "UPDATE EMPLOYEE "
							+"SET EMPID=?,NAME=?,AGE=?,GENDER=?,PHOTOID=?,ZIP=?,PREF=?,"
							+"ADDRESS=?,POSTID=?,ENTDATE=?,RETDATE=? WHERE ID = ?";
	private static final String DELETE_QUERY = "DELETE FROM EMPLOYEE WHERE ID = ?";

	/**
	 * ID指定の検索を実施する。
	 *
	 * @param id 検索対象のID
	 * @return 検索できた場合は検索結果データを収めたPostインスタンス。検索に失敗した場合はnullが返る。
	 */
	public Employee findById(int id) {
		Employee result = null;

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
			statement.setInt(1, id);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				result = processRow(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return result;
	}

	/**
	 * パラメータ指定の検索を実施する。
	 * 有効なパラメータ指定が1つも存在しない場合は全件検索になる。
	 *
	 * @param param 検索用のパラメータを収めたオブジェクト。
	 * @return 検索結果を収めたList。検索結果が存在しない場合は長さ0のリストが返る。
	 */
	public List<Employee> findByParam(Param param) {
		List<Employee> result = new ArrayList<>();

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
	 * 指定されたEmployeeオブジェクトを新規にDBに登録する。
	 * 登録されたオブジェクトにはDB上のIDが上書きされる。
	 * 何らかの理由で登録に失敗した場合、IDがセットされない状態（=0）で返却される。
	 *
	 * @param Employee 登録対象オブジェクト
	 * @return DB上のIDがセットされたオブジェクト
	 */
	public Employee create(Employee employee) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return employee;
		}

		try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[] { "ID" });) {
			// INSERT実行
			setParameter(statement, employee, false);
			statement.executeUpdate();

			// INSERTできたらKEYを取得
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);
			employee.setId(id);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return employee;
	}

	/**
	 * 指定されたEmployeeオブジェクトを使ってDBを更新する。
	 *
	 * @param employee 更新対象オブジェクト
	 * @return 更新に成功したらtrue、失敗したらfalse
	 */
	public Employee update(Employee employee) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return employee;
		}

		try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
			setParameter(statement, employee, true);
			statement.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return employee;
	}

	/**
	 * 指定されたIDのPostデータを削除する。
	 *
	 * @param id 削除対象のPostデータのID
	 * @return 削除が成功したらtrue、失敗したらfalse
	 */
	public boolean remove(int id) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return false;
		}

		int count = 0;
		try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
			// DELETE実行
			statement.setInt(1, id);
			count = statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}
		return count == 1;
	}

	/**
	 * 検索結果からオブジェクトを復元する。
	 *
	 * @param rs 検索結果が収められているResultSet。rs.next()がtrueであることが前提。
	 * @return 検索結果を収めたオブジェクト
	 * @throws SQLException 検索結果取得中に何らかの問題が発生した場合に送出される。
	 */
	private Employee processRow(ResultSet rs) throws SQLException {
		Employee result = new Employee();

		// Employee本体の再現
		result.setId(rs.getInt("ID"));
		result.setEmpId(rs.getString("EMPID"));
		result.setName(rs.getString("NAME"));
		result.setAge(rs.getInt("AGE"));
		result.setGenderByInt(rs.getInt("GENDER"));
		result.setPhotoId(rs.getInt("PHOTOID"));	// Photoオブジェクトに関しては必要になるまで生成しない
		result.setZip(rs.getString("ZIP"));
		result.setPref(rs.getString("PREF"));
		result.setAddress(rs.getString("ADDRESS"));
		Date entDate = rs.getDate("ENTDATE");
		if (entDate != null) {
			result.setEnterDate(entDate.toString());
		}
		Date retDate = rs.getDate("RETDATE");
		if (retDate != null) {
			result.setRetireDate(retDate.toString());
		}

		// 入れ子のオブジェクトの再現
		Post post = new Post();
		post.setId(rs.getInt("POSTID"));
		post.setName(rs.getString("POST_NAME"));
		result.setPost(post);

		return result;
	}

	/**
	 * オブジェクトからSQLにパラメータを展開する。
	 *
	 * @param statement パラメータ展開対象のSQL
	 * @param employee パラメータに対して実際の値を供給するオブジェクト
	 * @param forUpdate 更新に使われるならtrueを、新規追加に使われるならfalseを指定する。
	 * @throws SQLException パラメータ展開時に何らかの問題が発生した場合に送出される。
	 */
	private void setParameter(PreparedStatement statement, Employee employee, boolean forUpdate) throws SQLException {
		int count = 1;

		statement.setString(count++, employee.getEmpId());
		statement.setString(count++, employee.getName());
		statement.setInt(count++, employee.getAge());
		statement.setInt(count++, employee.getGender().ordinal());
		statement.setInt(count++, employee.getPhotoId());
		statement.setString(count++, employee.getZip());
		statement.setString(count++, employee.getPref());
		statement.setString(count++, employee.getAddress());
		statement.setInt(count++, employee.getPost().getId());
		if (employee.getEnterDate() != null) {
			statement.setDate(count++, Date.valueOf(employee.getEnterDate()));
		} else {
			statement.setDate(count++, null);
		}
		if (employee.getRetireDate() != null) {
			statement.setDate(count++, Date.valueOf(employee.getRetireDate()));
		} else {
			statement.setDate(count++, null);
		}

		if (forUpdate) {
			statement.setInt(count++, employee.getId());
		}
	}
}
