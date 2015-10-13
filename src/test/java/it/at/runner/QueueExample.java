package it.at.runner;

import java.util.LinkedList;
import java.util.Queue;

public class QueueExample {
	
	public static void main(String[] args) {
		
		final Queue<String> queue = new LinkedList<String>();
		
		queue.add("a");
		queue.add("b");
		queue.add("c");
		
		System.err.println(queue);
		
		queue.add(queue.remove());
		
		System.err.println(queue);
		
	}
	
}
