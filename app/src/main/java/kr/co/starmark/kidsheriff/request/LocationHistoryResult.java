package kr.co.starmark.kidsheriff.request;

import java.util.ArrayList;


public class LocationHistoryResult {
	String result;
	ArrayList<Location> list = new ArrayList<Location>(20);
	
	public void addLocation(Location loc)
	{
		list.add(loc);
	}

    @Override
    public String toString() {
        return "LocationHistoryResult{" +
                "result='" + result + '\'' +
                ", list=" + list +
                '}';
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ArrayList<Location> getList() {
        return list;
    }

    public void setList(ArrayList<Location> list) {
        this.list = list;
    }
}
