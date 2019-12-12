package energyhub.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3AccessImpl implements S3AccessInterface {

	@Override
	public List<String> getFileFromS3(String fullFileURL) throws Exception {

		AmazonS3URI s3URI = new AmazonS3URI(fullFileURL);

		String bucketName = s3URI.getBucket();
		String key = s3URI.getKey();
		S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;

		try {

			AWSCredentials creds = new AnonymousAWSCredentials();

			@SuppressWarnings("deprecation")
			AmazonS3 s3Client = new AmazonS3Client(creds);
			// Get an object and print its contents.
			fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
			List<String> fileAsList = downloadFileIntoStringList(fullObject.getObjectContent());
			return fileAsList;
		} catch (Exception e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process
			// it, so it returned an error response.
			return null;
		} finally {
			// To ensure that the network connection doesn't remain open, close any open
			// input streams.
			if (fullObject != null) {
				fullObject.close();
			}
			if (objectPortion != null) {
				objectPortion.close();
			}
			if (headerOverrideObject != null) {
				headerOverrideObject.close();
			}
		}

	}

	private List<String> downloadFileIntoStringList(InputStream input) throws IOException {
		// Read the text input stream one line at a time and load each line.
		List<String> downloadedFile = new ArrayList<String>();
		InputStream in = new GZIPInputStream(input);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line = null;
		while ((line = reader.readLine()) != null) {

			downloadedFile.add(line);

		}

		return downloadedFile;
	}

}
