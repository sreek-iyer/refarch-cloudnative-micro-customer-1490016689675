package customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
@ConfigurationProperties(prefix="spring.application.keyprotect")
public class KeyProtectPropertiesBean {

	public String getApiEndpoint() {
		return apiEndpoint;
	}
	public void setApiEndpoint(String apiEndpoint) {
		this.apiEndpoint = apiEndpoint;
	}
	public String getBluemixSpaceGuid() {
		return bluemixSpaceGuid;
	}
	public void setBluemixSpaceGuid(String bluemixSpaceGuid) {
		this.bluemixSpaceGuid = bluemixSpaceGuid;
	}
	public String getBluemixOrgGuid() {
		return bluemixOrgGuid;
	}
	public void setBluemixOrgGuid(String bluemixOrgGuid) {
		this.bluemixOrgGuid = bluemixOrgGuid;
	}
	public String getOauthToken() {
		return oauthToken;
	}
	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	private String apiEndpoint;
	private String bluemixSpaceGuid;
	private String bluemixOrgGuid;
	private String oauthToken;
	private String refreshToken;
	
	public String toString() {
		final Gson gson = new Gson();
		return gson.toJson(this);
	}
	
}
