package beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Photo {
	/**
	 * 保持データ
	 */
    private int id;
    private String fileName;
    private String contentType;
    private byte[] photo;
    private Date entryDate;

    /**
     * データベースの文字数制限
     */
    public static final int MAX_FILENAME_LENGTH = 40;
    public static final int MAX_CONTENTTYPE_LENGTH = 40;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	public void setPhoto(InputStream is) {
        // 画像データを読み込みます
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int len;
            // 画像データのロード
            while ((len = is.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            // バッファの取得
            photo = baos.toByteArray();
        } catch (IOException ioe) {
            photo = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
        }
    }

	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	/**
     * オブジェクトのデータが有効かどうか調べます。
     * @return 有効な場合は true を返す
     */
    public boolean isValidObject() {
        if ((fileName == null) || (fileName.getBytes().length > MAX_FILENAME_LENGTH)) {
            System.err.println("Photo: Bad file name.");
            return false;
        }
        if ((contentType == null) || (contentType.getBytes().length > MAX_CONTENTTYPE_LENGTH)) {
            System.err.println("Photo: Bad file content type.");
            return false;
        }
        if (photo == null) {
            System.err.println("Photo: Bad photo data.");
            return false;
        }
        if (entryDate == null) {
            System.err.println("Photo: Bad entry date.");
            return false;
        }
        return true;
    }
}
