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
	private int authId;
	private String empId ;






	public ExpParam(int statusId, String nameParam ,int authId , String empId) {
		this.setStatusId(statusId);
		this.setNameParam(nameParam);
		this.setAuthId(authId);
		this.setEmpId(empId);

	}


	public String getEmpId() {
		return empId;
	}


	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public int getAuthId() {
		return authId;
	}

	public void setAuthId(int authId) {
		this.authId = authId;
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
		if(authId == 2){

			whereClause.append(" AND ");

			whereClause.append("EXP.EMPID = ?");

		}
		if (statusId != 0) {

			whereClause.append(" AND ");

			whereClause.append("EXP.STATUS_ID = ?");
		}
		if (!nameParam.isEmpty()) {

			whereClause.append(" AND ");

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
		if(authId == 2){

			statement.setString(count++, empId);

		}
		if (statusId != 0) {
			statement.setInt(count++, statusId);
		}
		if (!nameParam.isEmpty()) {
			statement.setString(count++, nameParam);
		}
	}

}
