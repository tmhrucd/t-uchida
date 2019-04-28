package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.Post;
import beans.Status;

/**
 * 部署データを扱うDAO
 */
public class StatusDAO {


	/**着手済・着手中**/


	/**
	 * クエリ文字列
	 */
	private static final String SELECT_ALL_QUERY = "SELECT ID, STATUS FROM EXP_STATUS ORDER BY ID";


	/**
	 * 承認ステータスを取得する。
	 *
	 * @return DBに登録されている承認ステータスデータ全件を収めたリスト。途中でエラーが発生した場合は空のリストを返す。
	 */
	public List<Status> findAll() {
		List<Status> result = new ArrayList<>();

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		try (Statement statement = connection.createStatement();) {
			ResultSet rs = statement.executeQuery(SELECT_ALL_QUERY);

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
	 * 検索結果行をオブジェクトとして構成する。
	 * @param rs 検索結果が収められているResultSet
	 * @return 検索結果行の各データを収めたPostインスタンス
	 * @throws SQLException ResultSetの処理中発生した例外
	 */
	private Status processRow(ResultSet rs) throws SQLException {
		Status result = new Status();
		result.setId(rs.getInt("ID"));
		result.setStatus(rs.getString("STATUS"));
		return result;
	}





	/**未着手**/


	private static final String SELECT_BY_ID_QUERY = "SELECT ID, NAME FROM POST WHERE ID = ?";
	private static final String INSERT_QUERY = "INSERT INTO POST(NAME) VALUES (?)";
	private static final String UPDATE_QUERY = "UPDATE POST SET NAME = ? WHERE ID = ?";
	private static final String DELETE_QUERY = "DELETE FROM POST WHERE ID = ?";



	/**
	 * ID指定の検索を実施する。
	 *
	 * @param id 検索対象のID
	 * @return 検索できた場合は検索結果データを収めたPostインスタンス。検索に失敗した場合はnullが返る。
	 */
	public Post findById(int id) {
		Post result = null;

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
			statement.setInt(1, id);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
//				result = processRow(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return result;
	}

}
