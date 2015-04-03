package com.starmark.resource;

import lombok.Data;


@Data
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
}