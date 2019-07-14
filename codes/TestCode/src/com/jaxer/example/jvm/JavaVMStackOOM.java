package com.jaxer.example.jvm;

/**
 * 栈 OOM 测试
 * <p>
 * Created by jaxer on 2019-03-20
 */
public class JavaVMStackOOM {
	private void doStop() {
		while (true) {
		}
	}

	private void stackLeakByThread() {
		while (true) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					doStop();
				}
			}).start();
		}
	}

	public static void main(String[] args) {
		new JavaVMStackOOM().stackLeakByThread();
	}
}
