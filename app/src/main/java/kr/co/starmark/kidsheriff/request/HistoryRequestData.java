package kr.co.starmark.kidsheriff.request;

public class HistoryRequestData {
	String requestorId;
	String targetUserId;
	int limit = 0;

    public String getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "HistoryRequestData{" +
                "requestorId='" + requestorId + '\'' +
                ", targetUserId='" + targetUserId + '\'' +
                ", limit=" + limit +
                '}';
    }
}
