package com.example.fbreader.Bookfileadapter;

import androidx.annotation.Nullable;

import org.fbreader.book.Book;

import java.io.File;

public class BookFile {
    private String name;
    private String path;
    public BookFile(String name,String path,File file){
        this.name = name;
        this.path = path;
    }
    public String getName(){
        return name;
    }
    public String getPath(){
        return path;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPath(String path){
        this.path = path;
    }
}
