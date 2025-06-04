package com.kitapkosem.model;

import java.sql.Timestamp;

public class Activity {

    private int activityId;
    private int userId;
    private String activityType;
    private Integer targetItemId;
    private Integer secondaryTargetItemId;
    private Timestamp createdAt;

    private String commentSnippet;
    private String actorUsername;
    private String actorProfileAvatarUrl;
    private String activityMessage;
    private String targetItemLink;
    private String targetItemTitle;
    private String secondaryTargetItemLink;
    private String secondaryTargetItemTitle;

    public Activity() {
    }

    public Activity(int userId, String activityType, Integer targetItemId, Integer secondaryTargetItemId) {
        this.userId = userId;
        this.activityType = activityType;
        this.targetItemId = targetItemId;
        this.secondaryTargetItemId = secondaryTargetItemId;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Integer getTargetItemId() {
        return targetItemId;
    }

    public void setTargetItemId(Integer targetItemId) {
        this.targetItemId = targetItemId;
    }

    public Integer getSecondaryTargetItemId() {
        return secondaryTargetItemId;
    }

    public void setSecondaryTargetItemId(Integer secondaryTargetItemId) {
        this.secondaryTargetItemId = secondaryTargetItemId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Transient alanlar için getter/setter'lar
    public String getActorUsername() {
        return actorUsername;
    }

    public void setActorUsername(String actorUsername) {
        this.actorUsername = actorUsername;
    }

    public String getActorProfileAvatarUrl() {
        return actorProfileAvatarUrl;
    }

    public void setActorProfileAvatarUrl(String actorProfileAvatarUrl) {
        this.actorProfileAvatarUrl = actorProfileAvatarUrl;
    }

    public String getActivityMessage() {
        return activityMessage;
    }

    public void setActivityMessage(String activityMessage) {
        this.activityMessage = activityMessage;
    }

    public String getTargetItemLink() {
        return targetItemLink;
    }

    public void setTargetItemLink(String targetItemLink) {
        this.targetItemLink = targetItemLink;
    }

    public String getTargetItemTitle() {
        return targetItemTitle;
    }

    public void setTargetItemTitle(String targetItemTitle) {
        this.targetItemTitle = targetItemTitle;
    }

    public String getSecondaryTargetItemLink() {
        return secondaryTargetItemLink;
    }

    public void setSecondaryTargetItemLink(String secondaryTargetItemLink) {
        this.secondaryTargetItemLink = secondaryTargetItemLink;
    }

    public String getSecondaryTargetItemTitle() {
        return secondaryTargetItemTitle;
    }

    public void setSecondaryTargetItemTitle(String secondaryTargetItemTitle) {
        this.secondaryTargetItemTitle = secondaryTargetItemTitle;
    }

    public String getCommentSnippet() {
        return commentSnippet;
    }

    public void setCommentSnippet(String commentSnippet) {
        this.commentSnippet = commentSnippet;
    }
}
