package org.huh.run;

public class Thing {
	private static int count = 0;
	private String name;

	public Thing() {
		name = "i am thing with number " + count;
		count++;
	}
	
	public String whoAreYou(){
		checkMyself();
		return name;
	}
	
	public void checkMyself(){
		System.out.println("boe");
	}
}
