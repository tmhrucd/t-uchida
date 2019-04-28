package java_s04;

import java.io.InputStream;
import java.sql.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import beans.Employee;
import beans.Expense;
import beans.Gender;
import beans.Photo;
import beans.Post;
import dao.EmployeeDAO;
import dao.ExpParam;
import dao.ExpenseDAO;
import dao.Param;
import dao.PhotoDAO;
import dao.PostDAO;
import dao.StatusDAO;




/**
 * 従業員関連のサービス実装。
 * Servlet/JSPの実装とは異なり、画像についてはバイナリでなくpathベースで扱うものとする。
 */
@Path("expenses")
public class ExpenseResource {
	private final EmployeeDAO empDao = new EmployeeDAO();
	private final PhotoDAO photoDao = new PhotoDAO();
	private final PostDAO postDao = new PostDAO();


	private final ExpenseDAO expDao = new ExpenseDAO();
	private final StatusDAO statusDao = new StatusDAO();



	/**着手済・着手中**/

	/**
	 * クエリパラメータ指定による検索を実施する。
	 * 何も指定しない場合は全件検索になる。
	 *
	 * @return 取得した経費情報のリストをJSON形式で返す。データが存在しない場合は空のオブジェクトが返る。
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Expense> findByParam(@QueryParam("statusId") int statusId,
			@QueryParam("nameParam") String nameParam) {
		ExpParam param = new ExpParam(statusId, nameParam);
		return expDao.findByParam(param);
	}



	/**
	 * 新規申請情報を登録する。
	 *
	 * @param form 申請情報を収めたオブジェクト
	 * @return DB上のIDが振られた新規申請情報
	 * @throws WebApplicationException 入力データチェックに失敗した場合に送出される。
	 */

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Expense create(final FormDataMultiPart form) throws WebApplicationException {
		Expense expense = new Expense();

		expense.setId(0);
		String reportDateStr = form.getField("reportDate").getValue();
		if (reportDateStr != null && !reportDateStr.isEmpty()) {
			expense.setReportDate(reportDateStr);
		}

		String updateDateStr = form.getField("updateDate").getValue();
		if (updateDateStr != null && !updateDateStr.isEmpty()) {
			expense.setUpdateDate(updateDateStr);
		}
		expense.setEmpId(form.getField("reportEmpId").getValue());
//		expense.setName(form.getField("name").getValue());
		expense.setTitle(form.getField("title").getValue());
		expense.setMoney(Integer.parseInt(form.getField("money").getValue()));
		expense.setStatusId(1);
//		expense.setStatus("申請中");
		expense.setPlace(form.getField("place").getValue());
		expense.setUpdateEmpId(null);
//		expense.setUpdateName(form.getField("updateName").getValue());
		expense.setReason(form.getField("reason").getValue());


		if (!expense.isValidObject()) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}


		return expDao.create(expense);
	}




	/**未着手**/


	/**
	 * ID指定で従業員情報を取得する。
	 *
	 * @param id 取得対象の従業員のID
	 * @return 取得した従業員情報をJSON形式で返す。データが存在しない場合は空のオブジェクトが返る。
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Employee findById(@PathParam("id") int id) {
		return empDao.findById(id);
	}





	/**
	 * 指定した情報でDBを更新する。
	 *
	 * @param form 更新情報を含めた従業員情報
	 * @throws WebApplicationException 入力データチェックに失敗した場合に送出される。
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Employee update(@PathParam("id") int id,
			final FormDataMultiPart form) throws WebApplicationException {
		Employee employee = new Employee();

		employee.setId(id);
		employee.setEmpId(form.getField("empId").getValue());
		employee.setName(form.getField("name").getValue());
		employee.setAge(Integer.parseInt(form.getField("age").getValue()));
		String gender = form.getField("gender").getValue();
		employee.setGender(Gender.valueOf(gender));

		employee.setZip(form.getField("zip").getValue());
		employee.setPref(form.getField("pref").getValue());
		employee.setAddress(form.getField("address").getValue());

		String enterDateStr = form.getField("enterDate").getValue();
		if (enterDateStr != null && !enterDateStr.isEmpty()) {
			employee.setEnterDate(enterDateStr);
		}

		String retireDateStr = form.getField("retireDate").getValue();
		if (retireDateStr != null && !retireDateStr.isEmpty()) {
			employee.setRetireDate(retireDateStr);
		}

		if (!employee.isValidObject()) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		// Photo関連の処理
		String photoIdSrc = form.getField("photoId").getValue();
		int photoId = Integer.parseInt(photoIdSrc);
		FormDataBodyPart photoPart = form.getField("photo");
		if (!photoPart.getContentDisposition().getFileName().isEmpty()) {
			updatePhoto(photoId, photoPart);
		}
		employee.setPhotoId(photoId);

		// Post関連の処理
		int postId = Integer.parseInt(form.getField("postId").getValue());
		Post post = postDao.findById(postId);
		if (post == null) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		employee.setPost(post);

		return empDao.update(employee);
	}

	/**
	 * 指定したIDの社員情報を削除する。同時に画像データも削除する。
	 *
	 * @param id 削除対象の社員情報のID
	 */
	@DELETE
	@Path("{id}")
	public void remove(@PathParam("id") int id) {
		Employee employee = empDao.findById(id);
		empDao.remove(id);
		photoDao.remove(employee.getPhotoId());
	}

	@GET
	@Path("csv")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadCsv() {
		Param param = new Param(0, "", "");
		List<Employee> list = empDao.findByParam(param);

		String header = "ID,社員番号,名前,年齢,性別,写真ID,郵便番号,都道府県,住所,所属部署ID,入社日付,退社日付"
				+ System.getProperty("line.separator");
		StringBuffer csvContents = new StringBuffer(header);

		for (Employee employee : list) {
			String line = employee.toString() + System.getProperty("line.separator");
			csvContents.append(line);
		}

		return Response.status(Status.OK)
				.entity(csvContents.toString())
				.header("Content-disposition", "attachment; filename=employee.csv")
				.build();
	}

	/**
	 * Formから渡されたデータを使用してPhotoデータを登録する。
	 *
	 * @param photoPart Formから渡されたPhotoデータ
	 * @return 登録されてIDが振られたPhotoインスタンス
	 */
	private Photo createPhoto(FormDataBodyPart photoPart) {
		Photo photo = build(photoPart);

		return photoDao.create(photo);
	}

	/**
	 * Formから渡されたデータを使用してPhotoデータを更新する。
	 *
	 * @param photoId 更新対象のPhotoのID
	 * @param photoPart Formから渡されたPhotoデータ
	 * @return 正常に更新された場合はtrue、失敗した場合はfalse
	 */
	private boolean updatePhoto(int photoId, FormDataBodyPart photoPart) {
		Photo photo = build(photoPart);
		photo.setId(photoId);
		return photoDao.update(photo);
	}

	/**
	 * formから渡されたデータを使用してPhotoインスタンスを構築する。
	 *
	 * @param photoPart Formから渡されたPhotoデータ
	 * @return ID以外のフィールドに値がセットされたPhotoインスタンス
	 */
	private Photo build(FormDataBodyPart photoPart) {
		Photo photo = new Photo();
		ContentDisposition photoInfo = photoPart.getContentDisposition();

		photo.setFileName(photoInfo.getFileName());

		photo.setContentType(photoPart.getMediaType().toString());

		BodyPartEntity bodyPartEntity = (BodyPartEntity)photoPart.getEntity();
		InputStream in = bodyPartEntity.getInputStream();
		photo.setPhoto(in);

		photo.setEntryDate(new Date(System.currentTimeMillis()));
		return photo;
	}
}
