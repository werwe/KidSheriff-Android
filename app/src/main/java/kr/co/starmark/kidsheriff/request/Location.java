package kr.co.starmark.kidsheriff.request;


public class Location {
	private String date;
	private double lat;
	private double lng;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Location{" +
                "date='" + date + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
