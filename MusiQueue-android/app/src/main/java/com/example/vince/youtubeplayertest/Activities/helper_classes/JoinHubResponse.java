package com.example.vince.youtubeplayertest.Activities.helper_classes;

/**
 * Created by john on 3/1/2017.
 */

public class JoinHubResponse {
    public Boolean error;
    public String errorCode;
    public String errorMessage;

    private static class Result{
        private Integer hub_id;
        private Boolean is_creator;
    }

    private Result result;

    public Integer getHubId(){
        return result.hub_id;
    }
    public Boolean getCreator(){return this.result.is_creator;}
}
