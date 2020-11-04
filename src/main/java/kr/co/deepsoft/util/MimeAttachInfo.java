package kr.co.deepsoft.util;

public class MimeAttachInfo {

	public long startPos;					/** start offset	*/
	public long endPos;						/** end offset		*/
	public long lineCnt;						/** The count of line in attachment */
	
	public int contentType;					/**< Content Type					*/
	public int subContentType;				/**< Sub Content Type				*/
	public String encoding;					/**< Content-Transfer-Encoding		*/
	
	public String attachId;					/** Attach ID		*/
	public String fileName;					/** Attach Filename */
	
	public Integer mailIdx;
	public String result;
	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getMailIdx() {
		return mailIdx;
	}

	public void setMailIdx(Integer mailIdx) {
		this.mailIdx = mailIdx;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isCid = false;
	
}
