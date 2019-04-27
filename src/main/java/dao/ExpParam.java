package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAOにクエリーパラムを引き渡すためのオブジェクト。
 */
public class ExpParam {

	/**着手済・着手中**/

	private int statusId;
	private String nameParam;

	public ExpParam(int statusId, String nameParam) {
		this.setStatusId(statusId);
		this.setNameParam(nameParam);
	}

	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getNameParam() {
		return nameParam;
	}
	public void setNameParam(String nameParam) {
		this.nameParam = nameParam == null ? "" : "%"+nameParam+"%";
	}


	/**
	 * 登録されているパラメータの状態からWHERE句を生成する。
	 *
	 * @return SQLのWHERE句
	 */
	public String getWhereClause() {
		StringBuilder whereClause = new StringBuilder();
		if (statusId != 0) {
			if (whereClause.length() == 0) {

			} else {
				whereClause.append(" AND ");
			}
			whereClause.append("EXP.STATUS_ID = ?");
		}
		if (!nameParam.isEmpty()) {
			if (whereClause.length() == 0) {

			} else {
				whereClause.append(" AND ");
			}
			whereClause.append("EMP1.NAME LIKE ?");
		}

		// ORDER BYは最後に指定する
		whereClause.append(" ORDER BY EXP.ID");

		return whereClause.toString();
	}

	/**
	 * getWhereClauseメソッドで設定されたWHERE句を含むSQLにパラメータをセットする
	 *
	 * @param statement パラメータをセットする対象のPreparedStatement
	 * @throws SQLException パラメータの設定時に何らかの問題があった場合
	 */
	public void setParameter(PreparedStatement statement) throws SQLException {
		int count = 1;
		if (statusId != 0) {
			statement.setInt(count++, statusId);
		}
		if (!nameParam.isEmpty()) {
			statement.setString(count++, nameParam);
		}
	}


	/***未着手*/

	private final static String BASE_WHERE_CLAUSE = " WHERE ";



}
