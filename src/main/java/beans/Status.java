package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Status {
	private int id;
	private String status;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


}
