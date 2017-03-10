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
}