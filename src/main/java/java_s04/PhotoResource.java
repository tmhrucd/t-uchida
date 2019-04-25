package java_s04;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import beans.Photo;
import dao.PhotoDAO;

@Path("photos")
public class PhotoResource {
	private final PhotoDAO photoDao = new PhotoDAO();

	/**
	 * ID指定で画像情報を取得する。
	 *
	 * @param id 取得対象の画像のID
	 * @return 取得した画像を返す。データが存在しない場合はNoImage画像が返る。
	 * @throws IOException NoImage画像の読み込み時に問題があった場合に送出される
	 */
	@GET
	@Path("{id}")
	public Response findById(@PathParam("id") int id) throws IOException {
		Photo photo = photoDao.findById(id);
		if (photo == null) {
			return Response.status(404)
					.build();
		} else {
			return Response.status(200)
					.encoding(photo.getContentType())
					.entity(photo.getPhoto())
					.build();
		}
	}
}
