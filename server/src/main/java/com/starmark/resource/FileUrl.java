package com.starmark.resource;

import lombok.Data;

@Data
public class FileUrl {
    String url;

    public FileUrl()
    {
    }
    public FileUrl(String url) {
        this.url = url;
    }
}

