package com.cloud.jian.core.cost;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * <p>计算后缀表达式</p>
 * <p>计算方法：从左到右依次遍历表达式，遇到数字进栈，遇到符号，就将栈顶的两个数字弹出，进行运算，运算结果进栈，直到运算结束</p>
 * @author cloud
 */
public class CaculateEngine {

	/**
	 * 根据后缀表达式计算结果
	 * @param caculateContent
	 * @param factor
	 * @return
	 */
	public String execute(List<String> caculateContent, CaculateFactor factor) {
		Stack<String> stack = new Stack<String>();
		for (String s: caculateContent) {
			if (isOperator(s)) {
				String operatorToUse = s;
				if (!stack.isEmpty()) {
					String operateKey = stack.pop();
					String beOperatedKey = stack.pop();
					String operateNumber = factor.getFactorValue(operateKey).toString();
					String beOperatedNumber = factor.getFactorValue(beOperatedKey).toString();
					if(operateNumber == null || beOperatedNumber == null) {
						throw new RuntimeException("计算异常");
					}
					if (s.equals("/") && Integer.valueOf(operateNumber.toString()) == 0) {
						throw new RuntimeException("计算异常");
					}
					String caculatedValue = caculate(beOperatedNumber, operateNumber, operatorToUse, isRoundUp(operateKey, beOperatedKey, operatorToUse));
					//由于中途计算结果也要放入栈中变成计算因子，故要将该结果放入计算因子列表中，并为其分配一个uuid作为临时的key
					String randomTemporaryFactorKey = UUID.randomUUID().toString();
					factor.setFactorValue(randomTemporaryFactorKey, caculatedValue);
					stack.push(randomTemporaryFactorKey);
				}

				// 数字则压入栈中
			} else {
				stack.push(s);
			}
		}
		if (!stack.isEmpty()) {
			return factor.getFactorValue(stack.pop()).toString();
		}
		return null;
	}

	/**判断是否操作符*/
	private boolean isOperator(String oper) {
		return oper.equals("+") || oper.equals("-") || oper.equals("/") ||
				oper.equals("*") || oper.equals("(") || oper.equals(")");
	}

	/**结果计算*/
	private String caculate(String beOperatedNumber, String operateNumber, String operator, boolean isRoundUp) {
		switch (operator) {
			case "+":
				return CaculateUtils.jia(beOperatedNumber, operateNumber, isRoundUp);
			case "-":
				return CaculateUtils.jian(beOperatedNumber, operateNumber, isRoundUp);
			case "*":
				return CaculateUtils.cheng(beOperatedNumber, operateNumber, isRoundUp);
			case "/":
				return CaculateUtils.chu(beOperatedNumber, operateNumber, isRoundUp);
			default:
				throw new RuntimeException("成本计算异常");
		}
	}

	/**
	 * 是否需要向上取整，即1.2取2，2.3取3
	 */
	private boolean isRoundUp(String operateKey, String beOperatedKey, String operator) {
		boolean isAxD = "A".equals(beOperatedKey) && "D".equals(operateKey) && "*".equals(operator);
		boolean isDxA = "D".equals(beOperatedKey) && "A".equals(operateKey) && "*".equals(operator);
		return isAxD || isDxA;
	}

		
}
