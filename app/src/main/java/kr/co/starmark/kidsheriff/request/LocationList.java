package kr.co.starmark.kidsheriff.request;

import java.util.ArrayList;


public class LocationList {
	String result;
	ArrayList<Location> list = new ArrayList<Location>(20);
	
	public void addLocation(Location loc)
	{
		list.add(loc);
	}
}
