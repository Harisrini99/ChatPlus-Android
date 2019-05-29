package com.example.wanted.chatplus;

/**
 * Created by wanted on 26/11/18.
 */

public class Model {

    private  boolean isSelected;
    private  String animal;

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}