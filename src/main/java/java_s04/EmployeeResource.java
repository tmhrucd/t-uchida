package java_s04;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import beans.Employee;
import beans.Gender;
import beans.Photo;
import beans.Post;
import dao.EmployeeDAO;
import dao.Param;
import dao.PhotoDAO;
import dao.PostDAO;

/**
 * 従業員関連のサービス実装。
 * Servlet/JSPの実装とは異なり、画像についてはバイナリでなくpathベースで扱うものとする。
 */
@Path("employees")
public class EmployeeResource {
	private final EmployeeDAO empDao = new EmployeeDAO();
	private final PostDAO postDao = new PostDAO();
	private final PhotoDAO photoDao = new PhotoDAO();



	/**ログイン処理
	 * @throws IOException **/
	@POST
	@Path("login")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String login(final FormDataMultiPart form, @Context HttpServletRequest request , @Context HttpServletResponse response ) throws IOException {

		String empId = form.getField("loginId").getValue();
		String logPass = form.getField("loginPass").getValue();

		System.out.println(empId+logPass);

		Employee emp = empDao.findByEmpId(empId);

		String str="";

		String Pass = "";

		//IDで検索できなかった
		if(emp == null){
			str = "指定されたIDの社員は登録されていません";
		}
		//社員はいた
		else{

			Pass = empDao.getPassByEmpId(empId);

			//パスワードがあっている
			if(Pass.equals(logPass))
			{
				str = "ログインしました";

				/**権限ID取得**/
				String authId = empDao.getAuthByEmpId(empId);

				/**セッション発行**/
				HttpSession session = request.getSession(true);

				//10分間
				session.setMaxInactiveInterval(1*60);

				//社員ID格納
				session.setAttribute("empId", empId);

				//権限ID格納
				session.setAttribute("authId", authId);

				System.out.println("セッション開始できたよ");

			}else{
				str = "パスワードが間違っています";
			}
		}

		return str;
	}

	/**アウト処理
	 * @throws IOException **/
	@POST
	@Path("logout")
	@Produces("text/plain")
	public String logout(@Context HttpServletRequest request , @Context HttpServletResponse response ) throws IOException {


		/**セッション発行**/
		HttpSession session = request.getSession(true);

		/**セッション破棄**/
		session.invalidate();

		String str ="ok";

		return str;
	}


	/**セッション情報確認処理**/
	@GET
	@Path("session")
	@Produces("text/plain")
	public String session(@Context HttpServletRequest request , @Context HttpServletResponse response ) throws IOException {


				String SessionInf = "false";

				/**セッション取得**/
				HttpSession session = request.getSession(false);

				if(session != null){

					SessionInf = (String)session.getAttribute("empId");

					Employee emp = empDao.findByEmpId(SessionInf);

					SessionInf = emp.getName();

				}



		return SessionInf;
	}



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
	 * EMPID指定で従業員情報を取得する。
	 *
	 * @param empId 取得対象の従業員のEMPID
	 * @return 取得した従業員情報をJSON形式で返す。データが存在しない場合は空のオブジェクトが返る。
	 */
	@GET
	@Path("emp/{empId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Employee findByEMPId(@PathParam("empId") String empId) {
		return empDao.findByEmpId(empId);
	}


	/**
	 * クエリパラメータ指定による検索を実施する。
	 * 何も指定しない場合は全件検索になる。
	 *
	 * @param postId 部署ID。指定しない場合は0が入る。
	 * @param empId ログイン用の従業員ID。指定しない場合はnullが入る。
	 * @param nameParam 名前の一部を指定するためのパラメータ。指定しない場合はnullが入る。
	 * @return 取得した従業員情報のリストをJSON形式で返す。データが存在しない場合は空のオブジェクトが返る。
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Employee> findByParam(@QueryParam("postId") int postId,
			@QueryParam("empId") String empId,
			@QueryParam("nameParam") String nameParam) {
		Param param = new Param(postId, empId, nameParam);
		return empDao.findByParam(param);
	}

	/**
	 * 指定した従業員情報を登録する。
	 *
	 * @param form 従業員情報（画像含む）を収めたオブジェクト
	 * @return DB上のIDが振られた従業員情報
	 * @throws WebApplicationException 入力データチェックに失敗した場合に送出される。
	 */

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Employee create(final FormDataMultiPart form) throws WebApplicationException {
		Employee employee = new Employee();

		employee.setId(0);
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
		FormDataBodyPart photoPart = form.getField("photo");
		Photo photo = createPhoto(photoPart);
		if (photo.getId() == 0) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		employee.setPhotoId(photo.getId());

		// Post関連の処理
		int postId = Integer.parseInt(form.getField("postId").getValue());
		Post post = postDao.findById(postId);
		if (post == null) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		employee.setPost(post);

		return empDao.create(employee);
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
