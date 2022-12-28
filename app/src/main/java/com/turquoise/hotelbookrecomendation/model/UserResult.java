
package com.turquoise.hotelbookrecomendation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserResult {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("reviews")
    @Expose
    private List<Review> reviews = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Review> getBookings() {
        return reviews;
    }

    public void setBookings(List<Review> reviews) {
        this.reviews = reviews;
    }

}
