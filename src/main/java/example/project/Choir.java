package example.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class Choir {

	private String choirName;
	
	private Address address;
	
	public Choir()
	{
		
	}

	public String getChoirName() {
		return choirName;
	}

	public void setChoirName(String choirName) {
		this.choirName = choirName;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}	
}
