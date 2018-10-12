package com.tcs.pickupapp.data.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by muhammad.sohail on 5/4/2018.
 */

public class FeedBack {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("Feedback")
    @Expose
    private String feedback;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
