package kr.co.starmark.kidsheriff.resource;

public class ImageStoreInfo {
    String Date;
	double lat;
	double lng;
	String imgUrl;


    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "ImageStoreInfo{" +
                "Date='" + Date + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}