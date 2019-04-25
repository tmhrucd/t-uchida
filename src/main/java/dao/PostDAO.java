package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.Post;

/**
 * 部署データを扱うDAO
 */
public class PostDAO {
	/**
	 * クエリ文字列
	 */
	private static final String SELECT_ALL_QUERY = "SELECT ID, NAME FROM POST ORDER BY ID";
	private static final String SELECT_BY_ID_QUERY = "SELECT ID, NAME FROM POST WHERE ID = ?";
	private static final String INSERT_QUERY = "INSERT INTO POST(NAME) VALUES (?)";
	private static final String UPDATE_QUERY = "UPDATE POST SET NAME = ? WHERE ID = ?";
	private static final String DELETE_QUERY = "DELETE FROM POST WHERE ID = ?";

	/**
	 * 部署の全件を取得する。
	 *
	 * @return DBに登録されている部署データ全件を収めたリスト。途中でエラーが発生した場合は空のリストを返す。
	 */
	public List<Post> findAll() {
		List<Post> result = new ArrayList<>();

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
	 * 指定されたPostオブジェクトを新規にDBに登録する。
	 * 登録されたオブジェクトにはDB上のIDが上書きされる。
	 * 何らかの理由で登録に失敗した場合、IDがセットされない状態（=0）で返却される。
	 *
	 * @param post 登録対象オブジェクト
	 * @return DB上のIDがセットされたオブジェクト
	 */
	public Post create(Post post) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return post;
		}

		try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[] { "ID" });) {
			// INSERT実行
			statement.setString(1, post.getName());
			statement.executeUpdate();

			// INSERTできたらKEYを取得
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);
			post.setId(id);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return post;
	}

	/**
	 * 指定されたPostオブジェクトを使ってDBを更新する。
	 *
	 * @param post 更新対象オブジェクト
	 * @return 更新に成功したらtrue、失敗したらfalse
	 */
	public boolean update(Post post) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return false;
		}

		int count = 0;
		try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
			statement.setString(1, post.getName());
			statement.setInt(2, post.getId());
			count = statement.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return count == 1;
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
	 * 検索結果行をオブジェクトとして構成する。
	 * @param rs 検索結果が収められているResultSet
	 * @return 検索結果行の各データを収めたPostインスタンス
	 * @throws SQLException ResultSetの処理中発生した例外
	 */
	private Post processRow(ResultSet rs) throws SQLException {
		Post result = new Post();
		result.setId(rs.getInt("ID"));
		result.setName(rs.getString("NAME"));
		return result;
	}
}
