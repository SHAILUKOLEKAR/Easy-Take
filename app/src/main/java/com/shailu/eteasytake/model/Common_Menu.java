package com.shailu.eteasytake.model;

public class Common_Menu {
    private String Name, ItemName,ItemImage,Price;


    public Common_Menu() { }

    public Common_Menu(String name, String itemName, String itemImage, String price)
    {
        Name = name;
        ItemName = itemName;
        ItemImage = itemImage;
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemImage() {
        return ItemImage;
    }

    public void setItemImage(String itemImage) {
        ItemImage = itemImage;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
