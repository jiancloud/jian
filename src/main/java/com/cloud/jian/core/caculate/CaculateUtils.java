package com.cloud.jian.core.caculate;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public abstract class CaculateUtils {
	
	public static final String ZERO_DOUBLE_STRING = "0.00";
	public static final Double ZERO_DOUBLE_VALUE = 0.00;
	/**小数的位数*/
	public static final int DECIMALS = 0;
	/**向上转的模式*/
	public static final int ROUND_UP = BigDecimal.ROUND_UP;
	
	
	public static String jia(String beOperatedNumber, String operateNumber, boolean isRoundUp) {
		BigDecimal beOperated = new BigDecimal(beOperatedNumber);
		BigDecimal operate = new BigDecimal(operateNumber);
		BigDecimal result = null;
		if(isRoundUp) {
			result = beOperated.add(operate).setScale(DECIMALS, ROUND_UP);
		}else {
			result = beOperated.add(operate).setScale(DECIMALS);
		}
		return result.toString();
	}
	
	
	public static String jian(String beOperatedNumber, String operateNumber, boolean isRoundUp) {
		BigDecimal beOperated = new BigDecimal(beOperatedNumber);
		BigDecimal operate = new BigDecimal(operateNumber);
		BigDecimal result = null;
		if(isRoundUp) {
			result = beOperated.subtract(operate).setScale(DECIMALS, ROUND_UP);
		}else {
			result = beOperated.subtract(operate).setScale(DECIMALS);
		}
		return result.toString();
	}
	
	public static String cheng(String beOperatedNumber, String operateNumber, boolean isRoundUp) {
		BigDecimal beOperated = new BigDecimal(beOperatedNumber);
		BigDecimal operate = new BigDecimal(operateNumber);
		BigDecimal result = null;
		if(isRoundUp) {
			result = beOperated.multiply(operate).setScale(DECIMALS, ROUND_UP);
		}else {
			result = beOperated.multiply(operate).setScale(DECIMALS);
		}
		return result.toString();
	}

	public static String chu(String beOperatedNumber, String operateNumber, boolean isRoundUp) {
		BigDecimal beOperated = new BigDecimal(beOperatedNumber);
		BigDecimal operate = new BigDecimal(operateNumber);
		BigDecimal result = null;
		if(isRoundUp) {
			result = beOperated.divide(operate).setScale(DECIMALS, ROUND_UP);
		}else {
			result = beOperated.divide(operate).setScale(DECIMALS);
		}
		return result.toString();
	}
	
	public static String moneyFormat(String money) {
		DecimalFormat df = new DecimalFormat("#,###.00"); 
		if(ZERO_DOUBLE_STRING.equals(money)) {
			return ZERO_DOUBLE_STRING;
		}
		return df.format(Double.valueOf(money));
	}
	
	public static String moneyFormat(Double money) {
		DecimalFormat df = new DecimalFormat("#,###.00"); 
		if(ZERO_DOUBLE_VALUE.doubleValue() == money.doubleValue()) {
			return ZERO_DOUBLE_STRING;
		}
		return df.format(money);
	}
	
}
