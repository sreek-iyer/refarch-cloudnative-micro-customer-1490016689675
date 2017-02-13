package customer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Customer {
	
	@JsonIgnore
	public String getKeyId() {
		return keyId;
	}
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
	@JsonIgnore
	public String getIvId() {
		return ivId;
	}
	public void setIvId(String ivId) {
		this.ivId = ivId;
	}

	private String _id;
    
    private String _rev;
    
    private String username;
    private String password;
    
	private String firstName;
	private String lastName;
	private String imageUrl;
	
	private String keyId;
	private String ivId;
	
	public String getCustomerId() {
		return _id;
	}
	public void setCustomerId(String customerId) {
		this._id = customerId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

}
