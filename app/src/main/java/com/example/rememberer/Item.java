package com.example.rememberer;

class Item {

    private int id_item_history;
    private String name;
    private String img_name;
    private int rating;
    private double longitude;
    private double latitude;
    private int id_item;

    public Item(){

    }

    @Override
    public String toString() {
        return "Item{" +
                "id_item_history=" + id_item_history +
                ", name='" + name + '\'' +
                ", img_name='" + img_name + '\'' +
                ", rating=" + rating +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", id_item=" + id_item +
                '}';
    }

    public Item(int id_item_history, String name, String img_name,
                int rating, double longitude, double latitude, int id_item) {
        this.id_item_history = id_item_history;
        this.name = name;
        this.img_name = img_name;
        this.rating = rating;
        this.longitude = longitude;
        this.latitude = latitude;
        this.id_item = id_item;
    }

    public int getId_item_history() {
        return id_item_history;
    }

    public void setId_item_history(int id_item_history) {
        this.id_item_history = id_item_history;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getId_item() {
        return id_item;
    }

    public void setId_item(int id_item) {
        this.id_item = id_item;
    }
}
