package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Expense {
	/**
	 * データベースの文字数制限
	 */
	public static final int MAX_EMPID_LENGTH = 10;
	public static final int MAX_NAME_LENGTH = 40;


	public static final int MAX_TITLE_LENGTH = 255;
	public static final int MAX_PLACE_LENGTH = 255;
	public static final int MAX_REASON_LENGTH = 4000;

	/**
	 * 保持データ
	 */
	//申請ID
	private int id;
	//申請日
	private String reportDate;
	//更新日
	private String updateDate;
	//申請者社員ID
	private String empId;
	//申請者名
	private String name;
	//タイトル
	private String title;
	//金額
	private int money;
	//ステータスID
	private int statusId;
	//ステータス
	private String status;
	//支払先
	private String place;
	//更新者社員ID
	private String updateEmpId;
	//更新者名
	private String updateName;
	//却下理由
	private String reason;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getUpdateEmpId() {
		return updateEmpId;
	}

	public void setUpdateEmpId(String updateEmpId) {
		this.updateEmpId = updateEmpId;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * オブジェクトのデータが有効かどうか調べます。
	 * @return 有効な場合は true を返す
	 */
	public boolean isValidObject() {
		if ((empId == null) || (empId.getBytes().length > MAX_EMPID_LENGTH)) {
			System.err.println("Expense: Bad expense empId length.");
			return false;
		}
		if ((updateEmpId != null) && (updateEmpId.getBytes().length > MAX_EMPID_LENGTH)) {
			System.err.println("Expense: Bad expense updateEmpId length.");
			return false;
		}
		if ((title != null) && (title.getBytes().length > MAX_TITLE_LENGTH)) {
			System.err.println("Expense: Bad title length.");
			return false;
		}
		if ((place != null) && (place.getBytes().length > MAX_PLACE_LENGTH)) {
			System.err.println("Expense: Bad place length.");
			return false;
		}
		if ((reason != null) && (reason.getBytes().length > MAX_REASON_LENGTH)) {
			System.err.println("Expense: Bad reason length.");
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
        sb.append(this.getId());
        sb.append(",");
        String date = this.getReportDate();
        sb.append(date != null ? date : "");
        sb.append(",");
        date = this.getUpdateDate();
        sb.append(date != null ? date : "");
        sb.append(",");
        sb.append(this.getEmpId());
        sb.append(",");
        sb.append(this.getName());
        sb.append(",");
        sb.append(this.getTitle());
        sb.append(",");
        sb.append(this.getMoney());
        sb.append(",");
        sb.append(this.getStatusId());
        sb.append(",");
        sb.append(this.getStatus());
        sb.append(",");
        sb.append(this.getPlace());
        sb.append(",");
        sb.append(this.getUpdateEmpId());
        sb.append(",");
        sb.append(this.getUpdateName());
        sb.append(",");
        sb.append(this.getReason());


        // バッファに書き出します
		return sb.toString();
	}


}
