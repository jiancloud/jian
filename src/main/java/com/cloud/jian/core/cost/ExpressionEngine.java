package com.cloud.jian.core.cost;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>将中缀表达式转化为后缀表达式</p>
 * <p>
 * 转化方法：从左到右遍历，若是数字就输出，即成为后缀表达式的一部分，若是符号，则判断其与栈顶符号的优先级（右括号优先级最低），</br>
 * 是右括号或者优先级低于栈顶符号，则栈中符号依次弹出直到有栈中符号优先级低于当前符号，并将当前符号入栈
 * </p>
 * @author cloud
 */
public class ExpressionEngine {

	// 用于记录操作符
	private LinkedList<String> operators = new LinkedList<>();
	// 用于记录输出
	private List<String> output = new LinkedList<>();
	// 用于展示后缀表达式
	private StringBuilder expressionBuilder = new StringBuilder();
	
	/**
	 * 中缀表达式转为后缀表达式
	 * @param expression
	 * @return
	 */
	public List<String> transferToPostfix(String exp) {
		
		for(int i = 0, length = exp.length(); i < length; i++) {
			
			String next = String.valueOf(exp.charAt(i));
			
			//读入的为操作符
			if (isOperator(next)) {
				
				if (operators.isEmpty()) {
					operators.push(next);
				} else {
					
					// 如果读入的操作符为非")"且优先级比栈顶元素的优先级高或一样，则将操作符压入栈
					if (priority(operators.peek()) <= priority(next) && !next.equals(")")) {
						
						operators.push(next);
						
					// 如果读入的操作符为非")"且优先级比栈顶元素的优先级低，则将栈中元素依次弹出直到栈中元素优先级低于当前操作符
					// 然后再讲当前操作符入栈
					} else if (!next.equals(")") && priority(operators.peek()) > priority(next)) {
						
						while (operators.size() != 0 && priority(operators.peek()) >= priority(next) && 
							  !operators.peek().equals("(")) {
							String operator = operators.pop();
							expressionBuilder.append(operator);
							output.add(operator);
						}
						operators.push(next);
						
					// 如果读入的操作符是")"，则弹出从栈顶开始第一个"("及其之前的所有操作符
					} else if (next.equals(")")) {
						
						while (!operators.peek().equals("(")) {
							String operator = operators.pop();
							expressionBuilder.append(operator);
							output.add(operator);
						}
						// 弹出"("
						operators.pop();
					}
				}
			
			// 读入的为非操作符
			} else {
				expressionBuilder.append(next);
				output.add(next);
			}
		}
		
		if (!operators.isEmpty()) {
			Iterator<String> iterator = operators.iterator();
			while (iterator.hasNext()) {
				String operator = iterator.next();
				expressionBuilder.append(operator);
				output.add(operator);
				iterator.remove();
			}
		}
		
		System.out.println("后缀表达式： " + expressionBuilder.toString());
		System.out.println(output);
		return output;
	}


	// 判断是否操作符
	private boolean isOperator(String oper) {
		return oper.equals("+") || oper.equals("-") || oper.equals("/") || 
			   oper.equals("*") || oper.equals("(") || oper.equals(")");
	}

	// 计算操作符的优先级
	private int priority(String operator) {
		switch (operator) {
			case "+":
				return 1;
			case "-":
				return 1;
			case "*":
				return 2;
			case "/":
				return 2;
			case "(":
				return 3;
			case ")":
				return 3;
			default:
				return 0;
		}
	}
	
	public static void main(String [] args) {
		ExpressionEngine engine=new ExpressionEngine();
		/*for(String s: engine.transferToPostfix("(A*D-B)*C")) {
			System.out.println(s);
		}*/
		engine.transferToPostfix("(A+B)*C+D");
		engine.transferToPostfix("A+B*C+D");
	}
}
