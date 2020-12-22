package pCloudyJenkins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class AlterSuite implements IAlterSuiteListener {

	@Override
	public void alter(List<XmlSuite> suites) {

	    System.out.println("Strated......................"+System.getenv("pCloudy_Devices"));
		AlterSuite alterSuite = new AlterSuite();
		String jsonString = System.getenv("pCloudy_Devices");
		//String jsonString ="{\"devices\":[\"Samsung_GalaxyNote10_Android_10.0.0_f34e3\",\"Samsung_GalaxyA50_Android_10.0.0_310bf\"]}";
		//alterSuite.validatejSonString(jsonString);
		System.out.println(jsonString);
		List<Map<String, String>> deviceList = alterSuite.getDevices(jsonString);
		System.out.println(deviceList.size());
		XmlSuite suite = suites.get(0);
		suite.setThreadCount(deviceList.size());
		suite.setPreserveOrder(true);
		List<XmlTest> dynamictests = suite.getTests().stream().filter(xmlTest -> xmlTest.getName().startsWith("Test")).collect(Collectors.toList());
		dynamictests.get(0).getLocalParameters().put("device", deviceList.get(0).get("name"));
		
		List<XmlTest> clonedTests = new ArrayList<>();
		for (XmlTest each : dynamictests) {
			for (int i = 1; i < deviceList.size(); i++) {
				XmlTest cloned = new XmlTest(suite);
				cloned.setName(deviceList.get(i).get("name"));
				cloned.getLocalParameters().put("device", deviceList.get(i).get("name"));
				//cloned.getLocalParameters().put("version", deviceList.get(i).get("version"));
				cloned.getXmlClasses().addAll(each.getClasses());
				clonedTests.add(cloned);
			}
		}
		dynamictests.addAll(clonedTests);
	}
	
	/*public void validatejSonString(String jsonString) {
		if(jsonString.trim().isEmpty()) {
			System.out.println("Empty JsonString");
			throw new RuntimeException("Emty Device Json File");
		}

	}*/

	public static List<Map<String, String>> getDevices(String jsonString){
		List<Map<String, String>> deviceList = new ArrayList<Map<String, String>>();
		JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("devices");
		for(int i =0; i<jsonArray.length(); i++) {
			Map<String, String> device = new HashMap<String, String>() ;
			String fullName = jsonArray.getString(i);
			String[] deviceArray = fullName.split("_");
			device.put("name", fullName);
			deviceList.add(device);
			System.out.println("insdide---------------");
		}
		return deviceList;
	}

}

