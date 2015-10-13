package it.at.runner;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class BeeperControl {
	public void beep() {
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		
		final Runnable beeper = new Runnable() {
			public void run() {
				System.out.println("beep on " + new Date());
			}
		};
		
		final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 5, 2, TimeUnit.SECONDS);
		
		scheduler.schedule(new Runnable() {
			public void run() {
				System.out.println("call cancel on " + new Date());
				beeperHandle.cancel(true);
				
				scheduler.shutdown();
			}
		}, 30, TimeUnit.SECONDS);
		
		try {
			System.err.println("thread pool awaitTermination");
			scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
	}
	
	public static void main(String[] args) {
		new BeeperControl().beep();
	}
}