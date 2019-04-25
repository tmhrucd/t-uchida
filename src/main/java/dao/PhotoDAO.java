package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.Photo;

public class PhotoDAO {

	private static final String SELECT_BY_ID_QUERY = "SELECT ID, FILENAME, CONTTYPE, PHOTO, ENTDATE FROM PHOTO WHERE ID = ?";
	private static final String INSERT_QUERY = "INSERT INTO "
				+"PHOTO(FILENAME, CONTTYPE, PHOTO, ENTDATE) "
				+"VALUES(?,?,?,?)";
	private static final String UPDATE_QUERY = "UPDATE PHOTO "
				+"SET FILENAME=?, CONTTYPE=?, PHOTO=?, ENTDATE=? WHERE ID = ?";
	private static final String DELETE_QUERY = "DELETE FROM PHOTO WHERE ID = ?";

	/**
	 * ID指定の検索を実施する。
	 *
	 * @param id 検索対象のID
	 * @return 検索できた場合は検索結果データを収めたPostインスタンス。検索に失敗した場合はnullが返る。
	 */
	public Photo findById(int id) {
		Photo result = null;

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
	 * 画像の新規追加を実施する。
	 *
	 * @param photo 新規に追加する対象の画像に関する情報。
	 * @return DBによってIDを振られたPhotoインスタンス。失敗した場合はID=0で返る。
	 */
	public Photo create(Photo photo) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return photo;
		}
		try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[] { "ID" });) {
			// INSERT実行
			setParameter(statement, photo, false);
			statement.executeUpdate();;

			// INSERTできたらKEYを取得
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);
			photo.setId(id);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return photo;
	}

	/**
	 * 指定された画像の情報を更新する。
	 *
	 * @param photo 更新する対象の画像に関する情報。
	 * @return 削除に成功した場合はtrue、削除に失敗した場合はfalseを返す。
	 */
	public boolean update(Photo photo) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return false;
		}

		int count = 0;
		try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
			// UPDATE実行
			setParameter(statement, photo, true);
			count = statement.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return count == 1;
	}

	/**
	 * 指定された画像の情報を削除する。
	 * @param id 削除対象のID
	 * @return 削除に成功した場合はtrue、削除に失敗した場合はfalseを返す。
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
	 * ResultSetからオブジェクトを構成する。
	 *
	 * @param rs 検索結果を収めたResultSet。rs.next()==trueであることが前提。
	 * @return 検索結果を収めたPhotoインスタンス。
	 * @throws SQLException 検索結果取得中に何らかの問題が発生した場合に送出される。
	 */
	private Photo processRow(ResultSet rs) throws SQLException {
		Photo result = new Photo();

		result.setId(rs.getInt("ID"));
		result.setFileName(rs.getString("FILENAME"));
		result.setContentType(rs.getString("CONTTYPE"));
		result.setPhoto(rs.getBytes("PHOTO"));
		result.setEntryDate(rs.getDate("ENTDATE"));

		return result;
	}

	/**
	 * オブジェクトからSQLにパラメータを展開する。
	 *
	 * @param statement パラメータ展開対象のSQL
	 * @param photo パラメータに対して実際の値を供給するオブジェクト
	 * @param forUpdate 更新に使われるならtrueを、新規追加に使われるならfalseを指定する。
	 * @throws SQLException パラメータ展開時に何らかの問題が発生した場合に送出される。
	 */
	private void setParameter(PreparedStatement statement, Photo photo, boolean forUpdate) throws SQLException {
		int count = 1;

		statement.setString(count++, photo.getFileName());
		statement.setString(count++, photo.getContentType());
		statement.setBytes(count++, photo.getPhoto());
		statement.setDate(count++, photo.getEntryDate());

		if (forUpdate) {
			statement.setInt(count++, photo.getId());
		}
	}
}
