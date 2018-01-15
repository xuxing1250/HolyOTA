package com.hojy.www.hojyupgrader163.model;

/**
 * Created by Ron on 2016/10/27.
 */

public class Firmware {

    private String name;
    private long   size;
    private String path;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
