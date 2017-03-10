package customer.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import customer.config.KeyProtectPropertiesBean;

@Component
public class KeyProtectUtil {

    private static Logger logger =  LoggerFactory.getLogger(KeyProtectUtil.class);
	
	private final CloseableHttpClient httpclient;
	
	private KeyProtectUtil() {
		httpclient = HttpClients.createDefault();
	}
	@Autowired
	private KeyProtectPropertiesBean keyProtectProperties;
	
	
	@PostConstruct
	private void init() {
		System.out.println("Using KeyProtect properties: " + keyProtectProperties);
	}
	
	@Cacheable("keys")
	private String getKey(String keyId) throws URISyntaxException, ClientProtocolException, IOException {
		if (keyProtectProperties.getApiEndpoint() == null ||
			keyProtectProperties.getBluemixOrgGuid() == null ||
			keyProtectProperties.getBluemixSpaceGuid() == null ||
			keyProtectProperties.getOauthToken() == null) {
			// can't create a key -- no credentials
			logger.info("Can't get key: missing keyprotect properties");
			return null;
		}
		
		final URI uri = new URIBuilder()
				.setScheme("https")
				.setHost(keyProtectProperties.getApiEndpoint())
				.setPath("/api/v2/secrets/" + keyId)
				.build();
				
		logger.info("Calling: GET " + uri.toString());
		final HttpGet httpget = new HttpGet(uri);
		httpget.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
		httpget.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + keyProtectProperties.getOauthToken());
		httpget.addHeader("Bluemix-Space", keyProtectProperties.getBluemixSpaceGuid());
		httpget.addHeader("Bluemix-Org", keyProtectProperties.getBluemixOrgGuid());
		
		ResponseHandler<JsonObject> rh = new ResponseHandler<JsonObject>() {

			@Override
			public JsonObject handleResponse(
					final HttpResponse response) throws IOException {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				if (statusLine.getStatusCode() >= 300) {
					throw new HttpResponseException(
							statusLine.getStatusCode(),
							statusLine.getReasonPhrase());
				}
				if (entity == null) {
					throw new ClientProtocolException("Response contains no content");
				}
				ContentType contentType = ContentType.getOrDefault(entity);
				Charset charset = contentType.getCharset();
				Reader reader = new InputStreamReader(entity.getContent(), charset);
				
				final Gson gson = new Gson();
				final JsonObject obj = gson.fromJson(reader, JsonObject.class);				
				
				return obj;
			}
		};
		final JsonObject myjson = httpclient.execute(httpget, rh);	
		return myjson.get("resources").getAsJsonArray().get(0).getAsJsonObject().get("payload").getAsString();
	}
	
	/*
	 * Create a key, return the key Id
	 */
	public String createKey() throws ClientProtocolException, IOException, URISyntaxException {
		if (keyProtectProperties.getApiEndpoint() == null ||
				keyProtectProperties.getBluemixOrgGuid() == null ||
				keyProtectProperties.getBluemixSpaceGuid() == null ||
				keyProtectProperties.getOauthToken() == null) {
			// can't create a key -- no credentials
			logger.info("Not creating key: missing keyprotect properties");
			return null;
		}
		
		final URI uri = new URIBuilder()
				.setScheme("https")
				.setHost(keyProtectProperties.getApiEndpoint())
				.setPath("/api/v2/secrets")
				.build();
				
		logger.info("Calling: POST " + uri.toString());
		final HttpPost httppost = new HttpPost(uri);
		httppost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
		httppost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + keyProtectProperties.getOauthToken());
		httppost.addHeader("Bluemix-Space", keyProtectProperties.getBluemixSpaceGuid());
		httppost.addHeader("Bluemix-Org", keyProtectProperties.getBluemixOrgGuid());
		
		final JsonObject payload = new JsonObject();
		
		final JsonObject metadata = new JsonObject();
		metadata.addProperty("collectionType", "application/vnd.ibm.kms.secret+json");
		metadata.addProperty("collectionTotal", 1);
		payload.add("metadata", metadata);
		
		final JsonArray resourceArray = new JsonArray();
		final JsonObject resourceObj = new JsonObject();
		resourceObj.addProperty("type", "application/vnd.ibm.kms.secret+json");
		final UUID uuid = UUID.randomUUID();
		resourceObj.addProperty("name", uuid.toString());
	
		resourceArray.add(resourceObj);
		payload.add("resources",  resourceArray);
		
		final StringEntity payloadEntity = new StringEntity(payload.toString());
		
		httppost.setEntity(payloadEntity);
		
		final ResponseHandler<JsonObject> rh = new ResponseHandler<JsonObject>() {
			@Override
			public JsonObject handleResponse(
					final HttpResponse response) throws IOException {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				if (statusLine.getStatusCode() >= 300) {
					throw new HttpResponseException(
							statusLine.getStatusCode(),
							statusLine.getReasonPhrase());
				}
				if (entity == null) {
					throw new ClientProtocolException("Response contains no content");
				}
				ContentType contentType = ContentType.getOrDefault(entity);
				Charset charset = contentType.getCharset();
				
				logger.info("response: " + entity.toString());
				Reader reader = new InputStreamReader(entity.getContent(), charset);
				
				final Gson gson = new Gson();
				final JsonObject obj = gson.fromJson(reader, JsonObject.class);				
				logger.info("response: " + obj.toString());
			
				return obj;
			}
		};
		final JsonObject myjson = httpclient.execute(httppost, rh);	
		return myjson.get("resources").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
		
	}
	
	/*
	 * encrypt some text using some key id and iv id stored in key protect
	 */
	public String encrypt(String keyId, String plaintext) throws Exception {
		try {
			final String key = getKey(keyId);
			return EncryptUtil.encrypt(key, plaintext);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/*
	 * Delete a key
	 */
	public void deleteKey(String keyId) throws ClientProtocolException, IOException, URISyntaxException {
		if (keyProtectProperties.getApiEndpoint() == null ||
				keyProtectProperties.getBluemixOrgGuid() == null ||
				keyProtectProperties.getBluemixSpaceGuid() == null ||
				keyProtectProperties.getOauthToken() == null) {
			// can't create a key -- no credentials
			logger.info("Not deleting key: missing keyprotect properties");
			return;
		}
		
		final URI uri = new URIBuilder()
				.setScheme("https")
				.setHost(keyProtectProperties.getApiEndpoint())
				.setPath("/api/v2/secrets/" + keyId)
				.build();
				
		logger.info("Calling: DELETE " + uri.toString());
		final HttpDelete httpdelete = new HttpDelete(uri);
		httpdelete.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + keyProtectProperties.getOauthToken());
		httpdelete.addHeader("Bluemix-Space", keyProtectProperties.getBluemixSpaceGuid());
		httpdelete.addHeader("Bluemix-Org", keyProtectProperties.getBluemixOrgGuid());
		
		final ResponseHandler<JsonObject> rh = new ResponseHandler<JsonObject>() {
			@Override
			public JsonObject handleResponse(
					final HttpResponse response) throws IOException {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				if (statusLine.getStatusCode() >= 300) {
					throw new HttpResponseException(
							statusLine.getStatusCode(),
							statusLine.getReasonPhrase());
				}
		
				return null;
			}
		};
		httpclient.execute(httpdelete, rh);	
		
	}
	
	/*
	 * decrypt some text using a key id stored in key protect
	 */
	public String decrypt(String keyId, String encryptedText) throws Exception {
		try {
			final String key = getKey(keyId);
			
			return EncryptUtil.decrypt(key, encryptedText);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	
	}
	
	
}
