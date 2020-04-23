package asmb.dms;

import java.util.Map;

import groovy.json.JsonBuilder;

public class Utils {

	public static Map<String, Object> subMap(Map<String, Object> map, String key) {
		return (Map<String, Object>) map.get(key);
	}

	public static String subValue(Map<String, Object> map, String key) {
		return (String) map.get(key);
	}

	public static String toPrettyPrint(Object body) {
		return new JsonBuilder(body).toPrettyString();
	}

}
