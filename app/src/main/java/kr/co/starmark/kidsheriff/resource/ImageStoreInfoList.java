package kr.co.starmark.kidsheriff.resource;

import java.util.List;

public class ImageStoreInfoList {

    public String result;
    String userId;

    List<ImageStoreInfo> list = null;

    public ImageStoreInfoList() {
    }

    public ImageStoreInfoList(String userid) {
        this.userId = userid;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ImageStoreInfo> getList() {
        return list;
    }

    public void setList(List<ImageStoreInfo> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ImageStoreInfoList{" +
                "result='" + result + '\'' +
                ", userId='" + userId + '\'' +
                ", list=" + list +
                '}';
    }
}