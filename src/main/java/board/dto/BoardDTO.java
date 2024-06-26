package board.dto;

import java.sql.Timestamp;

public class BoardDTO {
	private int seq;
	private String title;
	private String contents;
	private int count ;
	private String member_id;
	private int board_code;
	private Timestamp write_date;
	private String secret;
	
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public int getBoard_code() {
		return board_code;
	}
	public void setBoard_code(int board_code) {
		this.board_code = board_code;
	}
	public Timestamp getWrite_date() {
		return write_date;
	}
	public void setWrite_date(Timestamp write_date) {
		this.write_date = write_date;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public BoardDTO(int seq, String title, String contents, int count, String member_id, int board_code,
			Timestamp write_date, String secret) {
		super();
		this.seq = seq;
		this.title = title;
		this.contents = contents;
		this.count = count;
		this.member_id = member_id;
		this.board_code = board_code;
		this.write_date = write_date;
		this.secret = secret;
	}
	public BoardDTO() {
		super();
	}
	
	
	

}
