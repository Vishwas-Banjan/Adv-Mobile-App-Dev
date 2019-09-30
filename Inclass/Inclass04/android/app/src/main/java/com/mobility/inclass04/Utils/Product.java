package com.mobility.inclass04.Utils;

public class Product {

    String name, region, imageUrl, id;
    Double price, discount;

    public Product(String name, String region, String imageUrl, String id, Double price, Double discount) {
        this.name = name;
        this.region = region;
        this.imageUrl = imageUrl;
        this.id = id;
        this.price = price;
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", id='" + id + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                '}';
    }

    public Product() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
