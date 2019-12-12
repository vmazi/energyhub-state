package energyhub.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public interface ReplayUtilInterface {

	public static final String fieldOptionParameter = "field";

	public static final String fieldMapKey = "fieldKey";

	public static final String fileLocationMapKey = "fileLocationKey";

	public static final String timeStampMapKey = "timeStampKey";

	public static final String changeTimeJSONKey = "changeTime";

	public static final String dateTimeSplitter = "T";

	public static final String dateSplitter = "-";

	public static final String fileType = ".jsonl.gz";

	public static final String fieldNotFoundMessage = "Data for field not found within data of two days from timestamp supplied";

	public static final String dateFormat = "yyyy-MM-dd";

	public static final String beforeJsonKey = "before";

	public static final String afterJsonKey = "after";

	public static final String s3Designator = "s3://";

	public Map<String, Object> getParameterMapFromCommandLine(String[] cmdArgs) throws Exception;

	public String getJSONFilePathFromTimeStamp(String timeStamp) throws Exception;

	public List<String> loadFileIntoMemoryAsList(String filePath);

	public int findIndexOfNearTimeStamp(List<String> fileContents, String timeStamp);

	public String findStateOfFieldAtTimeInFile(List<String> fileContents, String field, int startIndex);

	public String changeDateInTimeStamp(String timeStamp, int dateChange) throws ParseException;

	public String combineTimeStampFilePathAndLocationInput(String timeStampFilePath, String inputLocation);

	public String lookForValueOfFieldInFilesTwoDaysWithinTimeStampGiven(String field, String fileLocationInput,
			String timeStampInput) throws Exception;

	public JSONObject findStateWhenFileMissing(List<String> fieldOptions, String fileLocationInput,
			String timeStampInput) throws Exception;

	public JSONObject findStateWhenFileExists(List<String> fileContents, String timeStampInput,
			List<String> fieldOptions, String fileLocationInput) throws Exception;
}
