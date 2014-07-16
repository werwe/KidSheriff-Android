package kr.co.starmark.kidsheriff.resource;

public class FileUrl {
    String url;

    public FileUrl()
    {
    }
    public FileUrl(String url) {
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FileUrl{" +
                "url='" + url + '\'' +
                '}';
    }
}

