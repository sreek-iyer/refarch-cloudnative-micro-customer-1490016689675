package customer;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import customer.model.Customer;
import customer.util.EncryptUtil;


public class EncryptTest {
	
	
	@Test
	public void testEncrypt() throws Exception {
		final String plaintext = "abcdefgh";
		final String testKey = "+rHvI/C6AXVkBgqal+okun5IqECz1RjrsJNEOm7o5x8=";
		
		final String encrypted = EncryptUtil.encrypt(testKey, plaintext);
		
		System.out.println("encrypted: " + encrypted);
		
		final String decrypted = EncryptUtil.decrypt(testKey, encrypted);
		
		System.out.println("decrypted: " + decrypted);
		
		assert(plaintext.equals(decrypted));
		
	}
	
	@Test
	public void testMarshalFromJson() throws Exception {
		final String id = "asdgadsfads";
		
		final ObjectMapper mapper = new ObjectMapper();
		
		// construct a json string with the above properties
		
		final StringBuilder myJsonStr = new StringBuilder();
		
		myJsonStr.append("{");
		myJsonStr.append("\"customerId\":\"").append(id).append("\",");
		myJsonStr.append("\"email\":").append("\"my@email.com\"").append(",");
		myJsonStr.append("\"firstName\":").append("\"John\"").append(",");
		myJsonStr.append("\"lastName\":").append("\"Smith\"").append(",");
		myJsonStr.append("\"imageUrl\":").append("\"/image/myimage.jpg\"").append(",");
		myJsonStr.append("\"username\":").append("\"user1\"").append(",");
		myJsonStr.append("}");
		
		final String myJson = myJsonStr.toString();
		System.out.println("My JSON String:" + myJson);
		
		// marshall json to Customer object
		
		final Customer inv = mapper.readValue(myJson, Customer.class);
		
		// make sure all the properties match up
		assert(inv.getCustomerId().equals(id));
		assert(inv.getFirstName().equals("John"));
		assert(inv.getLastName().equals("Smith"));
		assert(inv.getImageUrl().equals("/image/myimage.jpg"));
		assert(inv.getUsername().equals("user1"));
		assert(inv.getEmail().equals("my@email.com"));
		
		
	}
}