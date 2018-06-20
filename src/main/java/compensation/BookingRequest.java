package compensation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookingRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	private String email;
	private List<String> elements = new ArrayList<>();

	public BookingRequest(String email) {
		super();
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getElements() {
		return elements;
	}

	public void setElements(List<String> elements) {
		this.elements = elements;
	}

	@Override
	public String toString() {
		return "BookingRequest [email=" + email + "]";
	}
}
