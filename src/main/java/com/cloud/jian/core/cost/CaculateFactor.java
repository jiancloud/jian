package com.cloud.jian.core.cost;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yangjian
 */
public class CaculateFactor{
	
	private Map<String, Object> staticFactorMap;
	private Map<String, Object> dynamicFactorMap;
	private Map<String, Object> factorMap;
	
	private Map<String, String> requestParam;
	
	public CaculateFactor(Map<String, String> requestParam) {
		this.requestParam = requestParam;
	}
	
	public void setFormulaFactor() {
		dynamicFactorMap = new HashMap<String, Object>();
		staticFactorMap = new HashMap<String, Object>(requestParam);
		factorMap = new HashMap<String, Object>(staticFactorMap.size() + dynamicFactorMap.size());
		factorMap.putAll(staticFactorMap);
		factorMap.putAll(dynamicFactorMap);
		filterFactors();
	}
	
	private void filterFactors() {
		Set<Map.Entry<String, Object>> entrySet = factorMap.entrySet();
		for(Map.Entry<String, Object> entry: entrySet) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if("D".equals(key) || "H".equals(key)) {
				String factorValue = value.toString();
				factorMap.put(key, String.valueOf(Double.valueOf(factorValue) / 100.0));
			}
		}
	}
	
	public Object getFactorValue(String factorKey) {
		return factorMap.get(factorKey);
	}
	
	public void setFactorValue(String key, Object value) {
		factorMap.put(key, value);
	}
	
}
