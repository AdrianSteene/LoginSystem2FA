package MedStore.MedRec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import MedStore.MedRec.crypt.MedRecCryptUtils;

@SpringBootTest
class MedRecApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void random2FAStringCheck() {
		String random = MedRecCryptUtils.random2FAString();
		assertEquals(random.length(), 6);
	}

}
