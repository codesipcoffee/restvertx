package example.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class Address {

	private String street;
	
	private String city;
	
	private String state;
	
	private String zipcode;
	
	public Address()
	{
		
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		zipcode = zipcode;
	}
	
	
}
