package kr.co.starmark.kidsheriff.request;



public class LocationUploadRequestData {
	private String userId;// email
	private Location loc;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "LocationUploadRequestData{" +
                "userId='" + userId + '\'' +
                ", loc=" + loc.toString() +
                '}';
    }
}