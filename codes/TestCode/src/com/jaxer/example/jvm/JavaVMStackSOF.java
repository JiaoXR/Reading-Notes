package com.jaxer.example.jvm;

/**
 * VM Args: -Xss160k
 * 栈 StackOverflowError 测试
 * <p>
 * Created by jaxer on 2019-03-20
 */
public class JavaVMStackSOF {
	private int stackLength = 1;

	private void stackLeak() {
		stackLength++;
		stackLeak();
	}

	public static void main(String[] args) {
		JavaVMStackSOF sof = new JavaVMStackSOF();
		try {
			sof.stackLeak();
		} catch (Throwable e) {
			System.out.println("stack length: " + sof.stackLength);
			throw e;
		}
	}
}
