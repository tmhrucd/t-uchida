package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee {
	/**
	 * データベースの文字数制限
	 */
	public static final int MAX_EMPID_LENGTH = 10;
	public static final int MAX_NAME_LENGTH = 40;
	public static final int MAX_ZIP_LENGTH = 10;
	public static final int MAX_PREF_LENGTH = 20;
	public static final int MAX_ADDRESS_LENGTH = 100;

	/**
	 * 保持データ
	 */
	private int id;
	private String empId;
	private String name;
	private int age;
	private Gender gender;
	private int photoId;
	private String zip;
	private String pref;
	private String address;
	private Post post;
	private String enterDate;
	private String retireDate;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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

	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public void setGenderByInt(int genderAsInt) {
		if (genderAsInt == 0) {
			this.gender = Gender.MAN;
		} else if (genderAsInt == 1) {
			this.gender = Gender.WOMAN;
		}
	}

	public int getPhotoId() {
		return photoId;
	}
	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}

	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPref() {
		return pref;
	}
	public void setPref(String pref) {
		this.pref = pref;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}

	public String getEnterDate() {
		return enterDate;
	}
	public void setEnterDate(String enterDate) {
		this.enterDate = enterDate;
	}

	public String getRetireDate() {
		return retireDate;
	}
	public void setRetireDate(String retireDate) {
		this.retireDate = retireDate;
	}

	/**
	 * オブジェクトのデータが有効かどうか調べます。
	 * @return 有効な場合は true を返す
	 */
	public boolean isValidObject() {
		if ((empId == null) || (empId.getBytes().length > MAX_EMPID_LENGTH)) {
			System.err.println("Employee: Bad employee id length.");
			return false;
		}
		if ((name == null) || (name.getBytes().length > MAX_NAME_LENGTH)) {
			System.err.println("Employee: Bad name length.");
			return false;
		}
		if ((gender != Gender.MAN) && (gender != Gender.WOMAN)) {
			System.err.println("Employee: Bad gender.");
			return false;
		}
		if ((zip != null) && (zip.getBytes().length > MAX_ZIP_LENGTH)) {
			System.err.println("Employee: Bad zip length.");
			return false;
		}
		if ((pref != null) && (pref.getBytes().length > MAX_PREF_LENGTH)) {
			System.err.println("Employee: Bad pref length.");
			return false;
		}
		if ((address != null) && (address.getBytes().length > MAX_ADDRESS_LENGTH)) {
			System.err.println("Employee: Bad address length.");
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
        sb.append(this.getId());
        sb.append(",");
        sb.append(this.getEmpId());
        sb.append(",");
        sb.append(this.getName());
        sb.append(",");
        sb.append(this.getAge());
        sb.append(",");
        sb.append(this.getGender());
        sb.append(",");
        sb.append(this.getPhotoId());
        sb.append(",");
        String s = this.getZip();
        sb.append(s != null ? s : "");
        sb.append(",");
        s = this.getPref();
        sb.append(s != null ? s : "");
        sb.append(",");
        s = this.getAddress();
        sb.append(s != null ? s : "");
        sb.append(",");
        sb.append(this.getPost().getId());
        sb.append(",");
        String date = this.getEnterDate();
        sb.append(date != null ? date : "");
        sb.append(",");
        date = this.getRetireDate();
        sb.append(date != null ? date : "");
        // バッファに書き出します
		return sb.toString();
	}


}
