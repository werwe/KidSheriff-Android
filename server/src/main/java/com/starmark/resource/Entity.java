package com.starmark.resource;

import java.util.List;

import lombok.Data;

@Data
public class Entity {
	private String result;
    private List<FileMeta> files;

  public Entity(String result,List<FileMeta> files) {
	  this.result = result;
	  this.files = files;
  }

  public Entity() {
  }
}