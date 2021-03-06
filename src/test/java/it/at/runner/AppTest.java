package it.at.runner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AppTest {
		
	public static void main(String[] args) {
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(2);
		
		taskExecutor.execute(new MyRunnable(2000));    	
		taskExecutor.execute(new MyRunnable(5000));
		taskExecutor.execute(new MyRunnable(10000));
		
		taskExecutor.shutdown();
		
		try {
			taskExecutor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}		
	}
	    
}

class MyRunnable implements Runnable {
	private long i;

	public MyRunnable(long i) {
		this.i = i;
	}

	@Override
	public void run() {
		System.err.println("i am " + this);
		
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		
		System.err.println("i am " + this + " ... ENDS !!?");
	}
}
