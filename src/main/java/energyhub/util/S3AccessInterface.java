package energyhub.util;

import java.io.IOException;
import java.util.List;

public interface S3AccessInterface {


	public List<String> getFileFromS3(String fullFileURL) throws Exception;

}