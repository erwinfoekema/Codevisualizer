package org.huh.run;

import java.util.ArrayList;
import java.util.List;

public class Test {
	List<Thing> things;

	public void sayHi() {
		System.out.println("Hi");
	}

	public void sayHo() {
		System.out.println("Ho");
	}

	public void sayHiHo() {
		sayHi();
		sayHo();
	}

	public void createThings() {
		things = new ArrayList<Thing>();
		things.add(new Thing());
		things.add(new Thing());
	}

	public void showThings(){
    for (Thing t:things){
    	System.out.println("application says: "+t.whoAreYou());
    }
  }

	public static void main(String[] args) {
		Test test = new Test();
		test.sayHiHo();
		test.createThings();
		test.showThings();
	}

}