package java_s04;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.Post;
import beans.Status;
import dao.StatusDAO;

/**
 * 承認ステータス関連のサービス実装。
 */
@Path("statuses")
public class StatusResource {
	private final StatusDAO dao = new StatusDAO();

	/**着手済・着手中**/

	/**
	 * 一覧用に承認情報を全件取得する。
	 * @return 承認情報のリストをJSON形式で返す。
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Status> findAll() {
		return dao.findAll();
	}




	/**未着手**/


	/**
	 * ID指定で部署情報を取得する。
	 *
	 * @param id 取得対象の部署のID
	 * @return 取得した部署情報をJSON形式で返す。
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Post findById(@PathParam("id") int id) {
		return dao.findById(id);
	}


}
