package com.uff.scimanager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtils {
	
	public static List<String> mapListMapToStringList(List<Map<String, Object>> resultSet) {
		List<String> results = new ArrayList<String>();
		
		for (Map<String, Object> resultMap : resultSet) {
			for (Map.Entry<String, Object> result : resultMap.entrySet()) {
			    results.add(result.getValue().toString());
			}
		}
		
		return results;
	}
	
}