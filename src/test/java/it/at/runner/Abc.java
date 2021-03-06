package it.at.runner;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Abc {

	public static void main(String[] args) {		
//		SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");		
//		System.out.println("Current Time: " + ft.format(new Date()));
		
		Calendar instance = GregorianCalendar.getInstance();
		
		instance.set(Calendar.HOUR_OF_DAY, 10);
		instance.set(Calendar.MINUTE, 33);
		instance.set(Calendar.SECOND, 42);
		
		Date time = instance.getTime();
		System.err.println(time);
		
		long timeInMillis = instance.getTimeInMillis();
		System.err.println(timeInMillis);
		
		System.err.println(new Date(timeInMillis));
		
		
		int n = 24 * 60 * 60;		
		System.err.println(n);
		
		Pattern patter = Pattern.compile("^[0-9]+ [0-9]{2}:[0-9]{2}:[0-9]{2}");
		Matcher matcher1 = patter.matcher("150120 10:18:22    48 Connect   u_systest@fw-test.at.test on");
		
		if (matcher1.find()) {
			System.err.println(matcher1.group());
		} else {
			System.err.println("matcher1 fail");
		}
		
		Matcher matcher2 = patter.matcher("                   49 Connect   u_systest@fw-test.at.test on");
		if (matcher2.find()) {
			System.err.println(matcher2.group());
		} else {
			System.err.println("matcher2 fail");
		}
		
		System.err.println(24L * 60 * 60 * 1000);
	}
}
