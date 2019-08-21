package asmb.dms;

import groovy.json.JsonOutput;

import java.util.List;
import java.util.Map;

public class Utils {

	public static Map<String, Object> subMap(Map<String, Object> map, String key) {
		return (Map<String, Object>) map.get(key);
	}

	public static String subValue(Map<String, Object> map, String key) {
		return (String) map.get(key);
	}

	public static String prettyPrint(Map<String, Object> map) {
		return JsonOutput.prettyPrint(JsonOutput.toJson(map));
	}

	public static String prettyPrint(List<?> list) {
		return JsonOutput.prettyPrint(JsonOutput.toJson(list));
	}

}
