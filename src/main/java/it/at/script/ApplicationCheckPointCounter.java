package it.at.script;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ApplicationCheckPointCounter {

	private static final BigDecimal BD_100 = new BigDecimal(100);
	private int totalNumber = 0;
	private int partialNumber = 0;

	public void totalNumber(int n) {
		this.totalNumber = n;
	}

	public void partialNumber(int m) {
		this.partialNumber += m;		
	}

	public String build() {
		return partialNumber + "/" + totalNumber + " (" + getPercentage(partialNumber, totalNumber) + " %)";
	}
	
	private BigDecimal getPercentage(int n, int total) {
	    float proportion = ((float) n) / ((float) total);
	    
	    return new BigDecimal(proportion).multiply(BD_100).setScale(2, RoundingMode.CEILING);
	}

}
