package energyhub.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;

public class ReplayUtilImpl implements ReplayUtilInterface {

	private static final String fieldDescription = "Field option paramter";

	static final String noFieldsExceptionMessage = "Must provide --field command line option";
	static final String errorParsingFileLocationTimeStamp = "Missing a File Location/TimeStamp or there are too many parameters passed";

	static final String timeStampIncorrectFormat = "Must provide timestamp in format YYYY-MM-DDTHH:MM:SS";

	public Map<String, Object> getParameterMapFromCommandLine(String[] cmdArgs) throws Exception {

		CommandLineParser parser = new DefaultParser();

		Options fieldOptionType = new Options();
		fieldOptionType.addOption(null, fieldOptionParameter, true, fieldDescription);

		CommandLine line = parser.parse(fieldOptionType, cmdArgs);
		if (!line.hasOption(fieldOptionParameter)) {
			throw new Exception(noFieldsExceptionMessage);
		}

		List<String> fieldOptions = Arrays.asList(line.getOptionValues(fieldOptionParameter));

		List<String> uriAndTimeStamp = line.getArgList();

		if (uriAndTimeStamp.size() != 2) {
			throw new Exception(errorParsingFileLocationTimeStamp);
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(fieldMapKey, fieldOptions);
		parameterMap.put(fileLocationMapKey, uriAndTimeStamp.get(0));
		parameterMap.put(timeStampMapKey, uriAndTimeStamp.get(1));

		return parameterMap;

	}

	public String getJSONFilePathFromTimeStamp(String timeStamp) throws Exception {

		String[] dateAndTime = timeStamp.split(dateTimeSplitter);
		if (dateAndTime.length != 2) {
			throw new Exception(timeStampIncorrectFormat);
		}
		String date = dateAndTime[0];

		// throw exceptions on impossible dates
		@SuppressWarnings("unused")
		SimpleDateFormat checkIfValidDate = new SimpleDateFormat(dateFormat);
		checkIfValidDate.setLenient(false);
		checkIfValidDate.parse(date);

		String[] dateBrokenDown = date.split(dateSplitter);
		if (dateBrokenDown.length != 3) {
			throw new Exception(timeStampIncorrectFormat);
		}
		StringBuilder filePath = new StringBuilder();
		filePath.append(dateBrokenDown[0]);
		filePath.append(File.separator);
		filePath.append(dateBrokenDown[1]);
		filePath.append(File.separator);
		filePath.append(dateBrokenDown[2]);
		filePath.append(fileType);
		return filePath.toString();
	}

	public List<String> loadFileIntoMemoryAsList(String filePath) {

		if (filePath.contains(s3Designator)) {

			S3AccessInterface s3AccessInterface = new S3AccessImpl();

			try {
				List<String> fileContents = s3AccessInterface.getFileFromS3(filePath);
				return fileContents;
			} catch (Exception e) {
				return null;
			}
		}

		try {
			InputStream in = new GZIPInputStream(new FileInputStream(filePath));

			Reader targetReader = new InputStreamReader(in);

			// Will be accessing file lines by index so force into an arrayList

			return new ArrayList<String>(IOUtils.readLines(targetReader));
		} catch (IOException e) {
			return null;
		}

	}

	public int findIndexOfNearTimeStamp(List<String> fileContents, String timeStamp) {

		JSONObject timeStampJson = new JSONObject();
		timeStampJson.put(changeTimeJSONKey, timeStamp);

		Comparator<String> searchJsonComp = new Comparator<String>() {

			public int compare(String p1, String p2) {
				JSONObject p1json = new JSONObject(p1);
				JSONObject p2json = new JSONObject(p2);

				return p1json.getString(changeTimeJSONKey).compareTo(p2json.getString(changeTimeJSONKey));
			}
		};

		int index = Collections.binarySearch(fileContents, timeStampJson.toString(), searchJsonComp);

		// if binary search fails, it returns (-(insertion point)-1)
		// so if it fails, simply undo the above and you will get the position that the
		// timestamp is
		// supposed to be at in the file
		if (index < 0) {
			index++;
			index = -index;

		}
		// this means that the index of the time youve searched belongs after eof, so
		// start searching from eof
		// in order to avoid accessing beyond length of file
		if (index > fileContents.size() - 1) {
			return fileContents.size() - 1;
		}

		return index;
	}

	@Override
	public String findStateOfFieldAtTimeInFile(List<String> fileContents, String field, int startIndex) {

		String stateFoundInBeforeField = null;

		// timeStamp given is before all times in file, look only in before States
		if (startIndex == 0) {
			return findStateOfFieldInBeforeStateFromIndexTillEOF(fileContents, startIndex, field);
		}
		// timeStamp given is after all times in file, look only in after States
		else if (startIndex == fileContents.size() - 1) {
			return findStateOfFieldInAfterStateFromIndexTillBOF(fileContents, startIndex, field);

		}

		// if timestamp lies within bounds of file, first look in before states, then
		// look in after states if before search fails
		stateFoundInBeforeField = findStateOfFieldInBeforeStateFromIndexTillEOF(fileContents, startIndex, field);
		if (stateFoundInBeforeField != null) {
			return stateFoundInBeforeField;
		} else {
			return findStateOfFieldInAfterStateFromIndexTillBOF(fileContents, startIndex, field);
		}

	}

	private String findStateOfFieldInBeforeStateFromIndexTillEOF(List<String> fileContents, int index, String field) {

		for (int i = index; i < fileContents.size(); i++) {
			JSONObject jsonLine = new JSONObject(fileContents.get(i));
			JSONObject beforeObj = jsonLine.getJSONObject(beforeJsonKey);
			if (beforeObj.has(field)) {
				return beforeObj.get(field).toString();
			}

		}
		return null;

	}

	private String findStateOfFieldInAfterStateFromIndexTillBOF(List<String> fileContents, int index, String field) {

		for (int i = index; i >= 0; i--) {
			JSONObject jsonLine = new JSONObject(fileContents.get(i));
			JSONObject afterObj = jsonLine.getJSONObject(afterJsonKey);
			if (afterObj.has(field)) {
				return afterObj.get(field).toString();
			}

		}
		return null;

	}

	public String changeDateInTimeStamp(String timeStamp, int dateChange) throws ParseException {

		String[] splitDate = timeStamp.split(dateTimeSplitter);
		String date = splitDate[0];

		Date dateParsed = new SimpleDateFormat(dateFormat).parse(date);

		DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

		dateParsed = DateUtils.addDays(dateParsed, dateChange);

		String strDate = dateFormatter.format(dateParsed);

		return strDate + dateTimeSplitter + splitDate[1];

	}

	public String combineTimeStampFilePathAndLocationInput(String timeStampFilePath, String inputLocation) {

		StringBuilder fullFilePath = new StringBuilder();

		if (inputLocation.contains(s3Designator)) {

			fullFilePath.append(inputLocation);
			// due to me using file.seperator, if on windows the default behavior uses the
			// wrong slash
			// furthermore since the s3 url already has an ending forward slash but the
			// filesystem
			// command does not, do not add an extra slash
			fullFilePath.append(timeStampFilePath.replace("\\", "/"));

		} else {
			fullFilePath.append(inputLocation);
			fullFilePath.append(File.separator);
			fullFilePath.append(timeStampFilePath);
		}
		return fullFilePath.toString();
	}

	private String lookForValueOfFieldInAfterStateOfDaysBefore(String field, String fileLocationInput, String timeStamp,
			int numOfDaysBefore) throws Exception {

		String newTimeStamp = changeDateInTimeStamp(timeStamp, -numOfDaysBefore);

		String newFilePathName = getJSONFilePathFromTimeStamp(newTimeStamp);

		String actualFullFilePath = combineTimeStampFilePathAndLocationInput(newFilePathName, fileLocationInput);

		List<String> fileContents = loadFileIntoMemoryAsList(actualFullFilePath);

		if (fileContents == null) {
			return null;
		}

		String state = findStateOfFieldInAfterStateFromIndexTillBOF(fileContents, fileContents.size() - 1, field);
		return state;

	}

	private String lookForValueOfFieldInBeforeStateOfDaysAfter(String field, String fileLocationInput, String timeStamp,
			int numOfDaysAfter) throws Exception {

		String newTimeStamp = changeDateInTimeStamp(timeStamp, numOfDaysAfter);

		String newFilePathName = getJSONFilePathFromTimeStamp(newTimeStamp);

		String actualFullFilePath = combineTimeStampFilePathAndLocationInput(newFilePathName, fileLocationInput);

		List<String> fileContents = loadFileIntoMemoryAsList(actualFullFilePath);

		if (fileContents == null) {
			return null;
		}

		String state = findStateOfFieldInBeforeStateFromIndexTillEOF(fileContents, 0, field);
		return state;

	}

	public String lookForValueOfFieldInFilesTwoDaysWithinTimeStampGiven(String field, String fileLocationInput,
			String timeStampInput) throws Exception {

		String state = lookForValueOfFieldInAfterStateOfDaysBefore(field, fileLocationInput, timeStampInput, 1);

		if (state == null) {
			state = lookForValueOfFieldInAfterStateOfDaysBefore(field, fileLocationInput, timeStampInput, 2);
			if (state == null) {
				state = lookForValueOfFieldInBeforeStateOfDaysAfter(field, fileLocationInput, timeStampInput, 1);
				if (state == null) {
					state = lookForValueOfFieldInBeforeStateOfDaysAfter(field, fileLocationInput, timeStampInput, 2);
				}
			}
		}
		return state;
	}

	public JSONObject findStateWhenFileMissing(List<String> fieldOptions, String fileLocationInput,
			String timeStampInput) throws Exception {

		JSONObject stateFound = new JSONObject();
		for (String field : fieldOptions) {

			String state = lookForValueOfFieldInFilesTwoDaysWithinTimeStampGiven(field, fileLocationInput,
					timeStampInput);

			if (state == null) {
				state = ReplayUtilInterface.fieldNotFoundMessage;
			}
			stateFound.putOpt(field, state);
		}
		return stateFound;
	}

	public JSONObject findStateWhenFileExists(List<String> fileContents, String timeStampInput,
			List<String> fieldOptions, String fileLocationInput) throws Exception {
		JSONObject stateFound = new JSONObject();

		int startIndex = findIndexOfNearTimeStamp(fileContents, timeStampInput);

		for (String field : fieldOptions) {

			String state = findStateOfFieldAtTimeInFile(fileContents, field, startIndex);

			// state not found within given file, look at files within two days of supplied
			// dates
			if (state == null) {

				state = lookForValueOfFieldInFilesTwoDaysWithinTimeStampGiven(field, fileLocationInput, timeStampInput);

				if (state == null) {
					state = ReplayUtilInterface.fieldNotFoundMessage;
				}
			}
			stateFound.putOpt(field, state);
		}
		return stateFound;
	}

}
