package kr.co.deepsoft.util;

import com.mail.smtp.util.codec.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class JNIJavaMail {
	
	private final Logger logger = LoggerFactory.getLogger(JNIJavaMail.class);

	public final static int OFFSET_HEADER_START = 0;		// 헤더시작 Offset
	public final static int OFFSET_HEADER_END = 1;			// 헤더종료 Offset
	public final static int OFFSET_TEXT_START = 2;			// 텍스트시작 Offset
	public final static int OFFSET_TEXT_END = 3;			// 텍스트종료 Offset
	public final static int OFFSET_MIME_HEADER_START = 4;	// Mime 헤더시작 Offset
	public final static int OFFSET_MIME_HEADER_END = 5;		// Mime 헤더종료 Offset
	public final static int OFFSET_MIME_BODY_START = 6;		// Mime 본문시작 Offset
	public final static int OFFSET_MIME_BODY_END = 7;		// Mime 본문종료 Offset
	public final static int OFFSET_DEPTH = 8;				// Depth 문자열 (1. 1.1 형식)
	public final static int OFFSET_CT = 9;					// Content Type
	public final static int OFFSET_SUB_CT = 10;				// SubContentType
	public final static int ENCODER = 11;					// Encoder
	public final static int LINE_ROW = 12;					// 라인수
	public final static int SUB_DEPTH = 13;					// 관련 EML 정보(Depth)
	public final static int CHARSET = 14;					// 문자셋
	public final static int CID = 15;						// CID
	public final static int ATTACH_NAME = 16;				// 첨부파일명
	
	private String USER_TEMP_PATH = "";						// DRM파일 TMP 경로
	private String USER_TEMP_PATH1 = "";						// DRM파일 TMP/TMP 경로

	/* encoder */
	public static final String _7BIT = "7";
	public static final String _8BIT = "8";
	public static final String B64 = "B";
	public static final String QP = "Q";
	public static final String BINARY = "b";

	/* content type */
	public static final int TEXT = 0;
	public static final int IMAGE = 1;
	public static final int AUDIO = 2;
	public static final int VIDEO = 3;
	public static final int APPLICATION	= 4;
	public static final int MULTIPART = 5;
	public static final int MESSAGE	= 6;

	/* sub content type */
	public static final int PLAIN = 0;
	public static final int HTML = 1;
	public static final int ALTERNATIVE = 2;
	public static final int MIXED = 3;
	public static final int REPORT = 4;
	public static final int RFC822 = 5;
	public static final int OCTETSTREAM = 6;
	public static final int DELIVERYSTATUS = 7;
	public static final int RELATED = 8;
	public static final int JPEG = 9;
	public static final int GIF = 10;
	public static final int X_MSVIDEO = 11;

	private native String GetLastError(long ptr);
	private native void InitAttachCursor(long ptr);
	private native long Open(long ptr, String emlPath, String msgFormat);
	private native boolean IsTNEF(long ptr);

	private native void Close(long ptr);

	// Get Attachment Info
	private native boolean GetNextAttach(
			long ptr,
			MimeAttachInfo info,
			String defaultFromCS,
			String toCS,
			String PEmlID);
	private native boolean GetNextCID(
			long ptr,
			MimeAttachInfo info,
			String defaultFromCS,
			String toCS,
			String PEmlID);

	// Get Attachment Contents
	private native long GetAttachContentSize(long ptr, MimeAttachInfo info, boolean decode);
	public native boolean OpenAttach(long ptr, MimeAttachInfo info);
	public native byte[] GetAttachContentNext(long ptr, StringBuffer result, boolean decode);
	public native void CloseAttach(long ptr);

	private native boolean GetHeader(long ptr, StringBuffer header, String PEmlID);
	private native boolean GetHeaderValue(long ptr, String hdrName, StringBuffer hdrValue, boolean decode, String defaultFromCS, String toCS, String PEmlID);
	private native boolean GetText(
			long ptr,
			StringBuffer text,
			boolean isHtml,
			String defaultFromCS,
			String fromCS,
			String toCS,
			boolean decode,
			String PEmlID);

	private native String GetMsgFormat(String emlPath);

	private long JNIptr = 0;
	private String msgFormat = "";
	private String[] msgFormatItem = null;
	private String emlPath = "";

	static {
		try
		{
			System.loadLibrary("dsJavaMail6");
		}
		catch( UnsatisfiedLinkError e )
		{
			e.printStackTrace();
		}

	}

	public JNIJavaMail()
	{

	}

	public void open(String emlPath) throws Exception
	{
		this.emlPath = emlPath;

		this.JNIptr = Open(this.JNIptr, emlPath, "");
		logger.debug("JNIptr :::: {} ", this.JNIptr);
		
		if(this.JNIptr < 0) {
			throw new Exception("EML File not found.");
		}
	}

	public void open(String emlPath, String msgFormat) throws Exception
	{
		this.emlPath = emlPath;
		this.msgFormat = msgFormat;
		this.msgFormatItem = this.msgFormat.split("");

		this.JNIptr = Open(this.JNIptr, emlPath, msgFormat);
		
		if(this.JNIptr < 0) {
			throw new Exception("EML File not found." + emlPath);
		}
	}

	public void close() throws Exception
	{
		if(this.JNIptr <= 0) {
			return;
		}

		Close(this.JNIptr);

		this.JNIptr = 0;
	}
	
	public void setUserTmpPath(String userTmpPath) throws Exception{
		this.USER_TEMP_PATH = userTmpPath;
	}
	
	public void setUserTmp1Path(String userTmpPath_TmpPath) throws Exception{
		this.USER_TEMP_PATH1 = userTmpPath_TmpPath;
	}
	
	public String getHeader(String PEmlID)
	throws Exception
	{
		StringBuffer header = new StringBuffer();

		if(this.JNIptr <= 0) {
			throw new Exception("Fail to get Header, because of don't open eml");
		}

		if(!GetHeader(this.JNIptr, header, PEmlID)) {
			throw new Exception("Fail to get Header, because of " + GetLastError(this.JNIptr));
		}

		return header.toString();
	}

	public String getHeaderValue(String hdrName, boolean decode, String defaultFromCS, String toCS, String PEmlID)
	throws Exception
	{
		StringBuffer hdrValue = new StringBuffer();

		if(this.JNIptr <= 0) {
			throw new Exception("Fail to get Header Value, because of don't open eml");
		}

		/*if(!GetHeaderValue(this.JNIptr, hdrName, hdrValue, decode, defaultFromCS, toCS, PEmlID)) {
			throw new Exception("Fail to get Header Value, because of " + GetLastError(this.JNIptr));
		}*/

		GetHeaderValue(this.JNIptr, hdrName, hdrValue, decode, defaultFromCS, toCS, PEmlID);

		return hdrValue.toString();
	}

	public String getContents(boolean isHtml, String defaultFromCS, String fromCS, String toCS, boolean decode, String PEmlID)
	throws Exception
	{
		StringBuffer text = new StringBuffer();

		if(this.JNIptr <= 0) {
			throw new Exception("Fail to get Contents, because of don't open eml");
		}

		if(!GetText(this.JNIptr, text, isHtml, defaultFromCS, fromCS, toCS, decode, PEmlID)) {
			throw new Exception("Fail to get Contents, because of " + GetLastError(this.JNIptr));
		}

		return text.toString();
	}

	public ArrayList<MimeAttachInfo> getAttachInfo(String defaultFromCS, String toCS, String PEmlID)
	throws Exception
	{
		ArrayList<MimeAttachInfo> attachInfoList = new ArrayList<MimeAttachInfo>();

		if(this.JNIptr <= 0) {
			throw new Exception("Fail to get Contents, because of don't open eml");
		}

		InitAttachCursor(this.JNIptr);

		for(;;) {
			MimeAttachInfo attachInfo = new MimeAttachInfo();
			if(!GetNextAttach(this.JNIptr, attachInfo, defaultFromCS, toCS, "")) break;

			attachInfoList.add(attachInfo);
		}

		return attachInfoList;
	}

	public ArrayList<MimeAttachInfo> getCIDInfo(String defaultFromCS, String toCS, String PEmlID)
	throws Exception
	{
		ArrayList<MimeAttachInfo> cidInfoList = new ArrayList<MimeAttachInfo>();

		if(this.JNIptr <= 0) {
			throw new Exception("Fail to get Contents, because of don't open eml");
		}

		InitAttachCursor(this.JNIptr);

		for(;;) {
			MimeAttachInfo cidInfo = new MimeAttachInfo();
			if(!GetNextCID(this.JNIptr, cidInfo, "UTF-8", "UTF-8", "")) break;

			cidInfoList.add(cidInfo);
		}

		return cidInfoList;
	}

	public void saveAttach(MimeAttachInfo info, String saveFileName, String savePath)
	throws Exception
	{
		saveAttach(info, saveFileName, savePath, false);
	}

	public void saveAttach(MimeAttachInfo info, String saveFileName, String savePath, boolean isFileNameEncoding)
	throws Exception
	{
		FileOutputStream fileOutputStream = null;

		try
		{
			String fileName = saveFileName;

			if( isFileNameEncoding )
			{
				fileName = Base64.encode(fileName.getBytes(), false);
				fileName = fileName.replaceAll("[/]", "_");
			}

			fileName = fileName.replaceAll("[\\\\/:*?\"<>|]","_");
			
			OpenAttach(this.JNIptr, info);

			byte[] bytes = null;
			fileOutputStream = new FileOutputStream(savePath + fileName);
			StringBuffer result = new StringBuffer();

			while(true)
			{
				bytes = GetAttachContentNext(this.JNIptr, result, true);

				if(result.toString().equals("false"))
				{
					break;
				}

				result.delete(0, result.capacity());
				fileOutputStream.write(bytes);
			}
		}
		catch( Exception e )
		{
			throw e;
		}
		finally
		{
			fileOutputStream.close();
			CloseAttach(this.JNIptr);
		}
	}

	public void getFilesFromEml(MimeAttachInfo info, String attachTempPath,int indx)
	throws Exception
	{
		
		try
		{
			OpenAttach(this.JNIptr, info);
//			info.fileName = indx + "_" + info.fileName;
					
			byte[] bytes = null;
			StringBuffer result = new StringBuffer();
			
			FileOutputStream foss = null;
			
//			String attachPath = attachTempPath + File.separator + info.fileName;//이렇게 하면 한글 폰트 설치가 안된 리눅스에서 에러 발생. 따라서 아래와 같이 바꿈.
			String attachPath = attachTempPath + File.separator + indx; 
			File attachFile = new File(attachPath);
				
			foss = new FileOutputStream(attachFile);
			
			while(true)
			{
				bytes = GetAttachContentNext(this.JNIptr, result, true);

				if(result.toString().equals("false"))
				{
					break;
				}
				
				result.delete(0, result.capacity());
				
				foss.write(bytes, 0 , bytes.length);
			}
			if(foss!=null) {foss.close();}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
        finally
		{
			CloseAttach(this.JNIptr);
		}
	}

	public long getAttachContentSize(MimeAttachInfo info)
	throws Exception
	{
		return GetAttachContentSize(this.JNIptr, info, true);
	}

	public long getAttachContentSize(MimeAttachInfo info, boolean decode)
	throws Exception
	{
		return GetAttachContentSize(this.JNIptr, info, decode);
	}

	public String getMsgFormat(String emlPath)
	throws Exception
	{
		return GetMsgFormat(emlPath);
	}

	public boolean getIsTNEF()
	throws Exception
	{
		return IsTNEF(this.JNIptr);
	}

	private byte[] getOffsetText(int sPos, int ePos)
	throws Exception
	{
		RandomAccessFile emlFile = null;
		byte[] bytes = null;

		try {
			int size = ePos - sPos;
			bytes = new byte[size];

			emlFile = new RandomAccessFile(this.emlPath, "r");
			emlFile.read(bytes, sPos, size);
		} catch (Exception e) {
			throw e;
		} finally {
			emlFile.close();
		}

		return bytes;
	}
}
