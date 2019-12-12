package energyhub.state;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import energyhub.util.*;

public class App {
	public static void main(String[] args) {

		try {

			ReplayUtilInterface utilityInterface = new ReplayUtilImpl();

			JSONObject stateFound;

			Map<String, Object> paramMap = utilityInterface.getParameterMapFromCommandLine(args);

			@SuppressWarnings("unchecked")
			List<String> fieldOptions = (List<String>) paramMap.get(ReplayUtilInterface.fieldMapKey);

			String timeStampInput = (String) paramMap.get(ReplayUtilInterface.timeStampMapKey);

			String fileLocationInput = (String) paramMap.get(ReplayUtilInterface.fileLocationMapKey);

			String jsonFilePath = utilityInterface.getJSONFilePathFromTimeStamp(timeStampInput);

			String actualFilePath = utilityInterface.combineTimeStampFilePathAndLocationInput(jsonFilePath,
					fileLocationInput);

			List<String> fileContents = utilityInterface.loadFileIntoMemoryAsList(actualFilePath);

			// if file not found, look for data in files within two days before and after
			// timestamp given
			if (fileContents == null) {

				stateFound = utilityInterface.findStateWhenFileMissing(fieldOptions, fileLocationInput, timeStampInput);
			}
			// file is found, look for data within file
			else {

				stateFound = utilityInterface.findStateWhenFileExists(fileContents, timeStampInput, fieldOptions,
						fileLocationInput);
			}
			JSONObject finalResult = new JSONObject();
			finalResult.put("state", stateFound);
			finalResult.put("ts", timeStampInput);
			System.out.println(finalResult.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
