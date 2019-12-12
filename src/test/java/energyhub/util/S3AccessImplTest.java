package energyhub.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class S3AccessImplTest {

	private S3AccessInterface s3Interface;

	@Before
	public void init() {

		s3Interface = new S3AccessImpl();

	}

	@Test
	public void testGetFile() {

		String URL = "s3://net.energyhub.assets/public/dev-exercises/audit-data/2016/01/01.jsonl.gz";

		try {

			List<String> testFile = s3Interface.getFileFromS3(URL);
			assert (!testFile.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

}
