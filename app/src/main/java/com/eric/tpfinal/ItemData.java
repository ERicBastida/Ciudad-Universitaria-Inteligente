package com.eric.tpfinal;

//Clase desarrollada simplemente para insertarlo dentro de un spinner item.

public class ItemData {

    String text;
    Integer imageId;

    public ItemData(String text, Integer imageId){
        this.text=text;
        this.imageId=imageId;
    }

    public String getText(){
        return text;
    }

    public Integer getImageId(){
        return imageId;
    }
}