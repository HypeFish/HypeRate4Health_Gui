package CLI.model;

import java.security.Timestamp;

public class Message {
	private String from;
	private String to;

	private String content;
	private Timestamp timestamp;

	public Message() {
	}


	public String getFrom() {
		return from;
	}

	public Message setFrom(String from) {
		this.from = from;
		return this;
	}

	public String getTo() {
		return to;
	}

	public Message setTo(String to) {
		this.to = to;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Message setContent(String content) {
		this.content = content;
		return this;
	}
}
