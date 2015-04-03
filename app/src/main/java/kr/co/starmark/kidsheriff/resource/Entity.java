package kr.co.starmark.kidsheriff.resource;

import java.util.List;


public class Entity {
	private String result;
    private List<FileMeta> files;

  public Entity(String result,List<FileMeta> files) {
	  this.result = result;
	  this.files = files;
  }

  public Entity() {
  }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<FileMeta> getFiles() {
        return files;
    }

    public void setFiles(List<FileMeta> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "result='" + result + '\'' +
                ", files=" + files +
                '}';
    }
}