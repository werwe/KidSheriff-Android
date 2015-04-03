package kr.co.starmark.kidsheriff.resource;



public class FileMeta {
    String name;
    long size;
    String url;
    String delete_url;  
    String delete_type;  

    public FileMeta(String filename, long size, String url) {
        this.name = filename;
        this.size = size;
        this.url = url;
        this.delete_url = url;
        this.delete_type = "DELETE";
    }

    public FileMeta() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDelete_url() {
        return delete_url;
    }

    public void setDelete_url(String delete_url) {
        this.delete_url = delete_url;
    }

    public String getDelete_type() {
        return delete_type;
    }

    public void setDelete_type(String delete_type) {
        this.delete_type = delete_type;
    }

    @Override
    public String toString() {
        return "FileMeta{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", delete_url='" + delete_url + '\'' +
                ", delete_type='" + delete_type + '\'' +
                '}';
    }
}