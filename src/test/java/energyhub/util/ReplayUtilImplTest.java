package energyhub.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReplayUtilImplTest {

	private ReplayUtilInterface cmdUtil;
	private String[] cmdArgs;
	private static String[] TestData = {
			"{\"changeTime\": \"2016-01-01T00:30:00.001059\", \"after\": {\"ambientTemp\": 79.0}, \"before\": {\"ambientTemp\": 77.0}}",
			"{\"changeTime\": \"2016-01-01T00:43:00.001064\", \"after\": {\"ambientTemp\": 80.0}, \"before\": {\"ambientTemp\": 79.0}}",
			"{\"changeTime\": \"2016-01-01T01:32:00.009816\", \"after\": {\"ambientTemp\": 81.0}, \"before\": {\"ambientTemp\": 80.0}}",
			"{\"changeTime\": \"2016-01-01T01:38:00.001038\", \"after\": {\"ambientTemp\": 82.0}, \"before\": {\"ambientTemp\": 81.0}}",
			"{\"changeTime\": \"2016-01-01T01:44:00.001145\", \"after\": {\"ambientTemp\": 81.0}, \"before\": {\"ambientTemp\": 82.0}}",
			"{\"changeTime\": \"2016-01-01T02:08:30.010956\", \"after\": {\"ambientTemp\": 79.0}, \"before\": {\"ambientTemp\": 81.0}}",
			"{\"changeTime\": \"2016-01-01T02:47:30.002413\", \"after\": {\"ambientTemp\": 77.0}, \"before\": {\"ambientTemp\": 79.0}}",
			"{\"changeTime\": \"2016-01-01T03:02:30.001424\", \"after\": {\"ambientTemp\": 78.0}, \"before\": {\"ambientTemp\": 77.0}}",
			"{\"changeTime\": \"2016-01-01T03:08:00.007712\", \"after\": {\"ambientTemp\": 80.0}, \"before\": {\"ambientTemp\": 78.0}}",
			"{\"changeTime\": \"2016-01-01T03:12:30.008936\", \"after\": {\"ambientTemp\": 79.0}, \"before\": {\"ambientTemp\": 80.0}}",
			"{\"changeTime\": \"2016-01-01T03:18:30.001950\", \"after\": {\"schedule\": true}, \"before\": {\"schedule\": false}}",
			"{\"changeTime\": \"2016-01-01T03:24:30.001180\", \"after\": {\"setpoint\": {\"heatTemp\": 67.0}}, \"before\": {\"setpoint\": {\"heatTemp\": 69.0}}}",
			"{\"changeTime\": \"2016-01-01T03:44:30.002761\", \"after\": {\"ambientTemp\": 77.0}, \"before\": {\"ambientTemp\": 79.0}}",
			"{\"changeTime\": \"2016-01-01T04:36:00.033185\", \"after\": {\"lastAlertTs\": \"2016-01-01T04:36:00.033185\"}, \"before\": {\"lastAlertTs\": \"2015-12-31T06:31:00.005702\"}}",
			"{\"changeTime\": \"2016-01-01T06:20:00.025884\", \"after\": {\"ambientTemp\": 79.0}, \"before\": {\"ambientTemp\": 77.0}}",
			"{\"changeTime\": \"2016-01-01T06:48:00.004545\", \"after\": {\"lastAlertTs\": \"2016-01-01T06:48:00.004545\"}, \"before\": {\"lastAlertTs\": \"2016-01-01T04:36:00.033185\"}}",
			"{\"changeTime\": \"2016-01-01T09:57:00.008158\", \"after\": {\"ambientTemp\": 77.0}, \"before\": {\"ambientTemp\": 79.0}}",
			"{\"changeTime\": \"2016-01-01T11:14:00.001492\", \"after\": {\"ambientTemp\": 78.0}, \"before\": {\"ambientTemp\": 77.0}}",
			"{\"changeTime\": \"2016-01-01T11:17:30.001484\", \"after\": {\"setpoint\": {\"heatTemp\": 68.0}}, \"before\": {\"setpoint\": {\"heatTemp\": 67.0}}}",
			"{\"changeTime\": \"2016-01-01T11:29:30.010615\", \"after\": {\"setpoint\": {\"heatTemp\": 66.0}}, \"before\": {\"setpoint\": {\"heatTemp\": 68.0}}}",
			"{\"changeTime\": \"2016-01-01T11:37:30.001219\", \"after\": {\"ambientTemp\": 79.0}, \"before\": {\"ambientTemp\": 78.0}}",
			"{\"changeTime\": \"2016-01-01T14:40:00.002881\", \"after\": {\"ambientTemp\": 77.0}, \"before\": {\"ambientTemp\": 79.0}}",
			"{\"changeTime\": \"2016-01-01T14:43:00.004061\", \"after\": {\"ambientTemp\": 76.0}, \"before\": {\"ambientTemp\": 77.0}}",
			"{\"changeTime\": \"2016-01-01T15:01:00.002184\", \"after\": {\"lastAlertTs\": \"2016-01-01T15:01:00.002184\"}, \"before\": {\"lastAlertTs\": \"2016-01-01T06:48:00.004545\"}}",
			"{\"changeTime\": \"2016-01-01T15:18:00.002331\", \"after\": {\"ambientTemp\": 78.0}, \"before\": {\"ambientTemp\": 76.0}}",
			"{\"changeTime\": \"2016-01-01T16:30:00.003211\", \"after\": {\"ambientTemp\": 77.0}, \"before\": {\"ambientTemp\": 78.0}}",
			"{\"changeTime\": \"2016-01-01T17:27:00.001050\", \"after\": {\"lastAlertTs\": \"2016-01-01T17:27:00.001050\"}, \"before\": {\"lastAlertTs\": \"2016-01-01T15:01:00.002184\"}}",
			"{\"changeTime\": \"2016-01-01T17:40:30.006346\", \"after\": {\"ambientTemp\": 79.0}, \"before\": {\"ambientTemp\": 77.0}}",
			"{\"changeTime\": \"2016-01-01T18:48:00.001030\", \"after\": {\"ambientTemp\": 78.0}, \"before\": {\"ambientTemp\": 79.0}}",
			"{\"changeTime\": \"2016-01-01T18:50:00.001729\", \"after\": {\"ambientTemp\": 76.0}, \"before\": {\"ambientTemp\": 78.0}}",
			"{\"changeTime\": \"2016-01-01T19:39:30.001625\", \"after\": {\"setpoint\": {\"coolTemp\": 76.0}}, \"before\": {\"setpoint\": {\"coolTemp\": 75.0}}}",
			"{\"changeTime\": \"2016-01-01T19:42:30.001205\", \"after\": {\"ambientTemp\": 75.0}, \"before\": {\"ambientTemp\": 76.0}}",
			"{\"changeTime\": \"2016-01-01T20:21:30.001867\", \"after\": {\"ambientTemp\": 77.0}, \"before\": {\"ambientTemp\": 75.0}}",
			"{\"changeTime\": \"2016-01-01T20:47:00.001259\", \"after\": {\"setpoint\": {\"coolTemp\": 74.0}}, \"before\": {\"setpoint\": {\"coolTemp\": 76.0}}}",
			"{\"changeTime\": \"2016-01-01T21:48:30.001210\", \"after\": {\"ambientTemp\": 79.0}, \"before\": {\"ambientTemp\": 77.0}}" };

	@Before
	public void init() {

		cmdUtil = new ReplayUtilImpl();

	}

	@Test
	public void testExceptionThrownIfNoFields() {

		cmdArgs = new String[] { "no", "fields", "given" };

		try {
			cmdUtil.getParameterMapFromCommandLine(cmdArgs);
			fail("Expected an Exception with message noFieldsExceptionMessage to be thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), ReplayUtilImpl.noFieldsExceptionMessage);
		}
	}

	@Test
	public void testExceptionOnFileLocationTimestampError() {

		cmdArgs = new String[] { "--field", "ambientTemp", "--field", "schedule", "2016-01-01T03:00" };

		try {
			cmdUtil.getParameterMapFromCommandLine(cmdArgs);
			fail("Expected an Exception with message errorParsingFileLocationTimeStamp to be thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), ReplayUtilImpl.errorParsingFileLocationTimeStamp);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFieldsParsedCorrectly() {

		cmdArgs = new String[] { "--field", "ambientTemp", "--field", "schedule", "/tmp/ehub_data",
				"2016-01-01T03:00" };

		try {
			Map<String, Object> paramMap = cmdUtil.getParameterMapFromCommandLine(cmdArgs);

			List<String> fields = (List<String>) paramMap.get(ReplayUtilInterface.fieldMapKey);
			assertEquals(fields.size(), 2);
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void testExceptionOnTimeStampIncorrectFormat() {

		try {
			cmdUtil.getJSONFilePathFromTimeStamp("GarbagetimeStamp");
			fail("Expected an Exception with message errorParsingFileLocationTimeStamp to be thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), ReplayUtilImpl.timeStampIncorrectFormat);
		}
	}

	@Test
	public void testExceptionOnTimeStampAlmostCorrect() {

		try {
			cmdUtil.getJSONFilePathFromTimeStamp("2016-2-3-StillgarbageTimestamp");
			fail("Expected an Exception with message errorParsingFileLocationTimeStamp to be thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), ReplayUtilImpl.timeStampIncorrectFormat);
		}
	}

	@Test
	public void testCorrectFormatImpossibleDate() {

		try {
			cmdUtil.getJSONFilePathFromTimeStamp("2016-02-31T03:00");
			fail("Expected a ParseException to be thrown");
		} catch (Exception e) {  
                  assertTrue( e instanceof ParseException);
}
	}

	@Test
	public void testTimeStampForFileNameParsedCorrectly() {

		StringBuilder testFileName = new StringBuilder();
		testFileName.append("2016");
		testFileName.append(File.separator);
		testFileName.append("02");
		testFileName.append(File.separator);
		testFileName.append("03");
		testFileName.append(ReplayUtilImpl.fileType);
		try {
			String fileNameformTimeStamp = cmdUtil.getJSONFilePathFromTimeStamp("2016-02-03T03:00");
			assertEquals(testFileName.toString(), fileNameformTimeStamp);
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void testFindNearIndexForGivenTimeStamp() {

		List<String> testerList = Arrays.asList(TestData);
		try {

			int testindex = cmdUtil.findIndexOfNearTimeStamp(testerList, "2016-01-01T03:00");

			Assert.assertTrue((testindex == 6 || testindex == 7));

		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void testFindStateOfFieldAtTimeInFileWhenFieldInBefore() {

		List<String> testerList = Arrays.asList(TestData);

		try {
			String state = cmdUtil.findStateOfFieldAtTimeInFile(testerList, "ambientTemp", 7);
			assertEquals(state, "77.0");
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void testFindStateOfFieldAtTimeInFileWhenFieldInAfter() {

		List<String> testerList = Arrays.asList(TestData);

		try {
			String state = cmdUtil.findStateOfFieldAtTimeInFile(testerList, "schedule", 12);
			assertEquals(state, "true");
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void testFindStateOfFieldAtTimeStampEarlierThanAllInFile() {

		List<String> testerList = Arrays.asList(TestData);

		try {
			String state = cmdUtil.findStateOfFieldAtTimeInFile(testerList, "ambientTemp", 0);
			assertEquals(state, "77.0");
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void testFindStateOfFieldAtTimeStampLaterThanAllInFile() {

		List<String> testerList = Arrays.asList(TestData);

		try {
			String state = cmdUtil.findStateOfFieldAtTimeInFile(testerList, "ambientTemp", testerList.size() - 1);
			assertEquals(state, "79.0");
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void testFieldsNotFoundInData() {

		List<String> testerList = Arrays.asList(TestData);

		try {
			String state = cmdUtil.findStateOfFieldAtTimeInFile(testerList, "garbageField", 10);
			assertEquals(state, null);
		} catch (Exception e) {
			fail();
		}

	}
	
	@Test
	public void testChangeDateInTimeStamp() {


		try {
			String newDate = cmdUtil.changeDateInTimeStamp("2016-01-01T03:00:00", -1) ;
			assertEquals(newDate, "2015-12-31T03:00:00");
		} catch (Exception e) {
			fail();
		}

	}

}
