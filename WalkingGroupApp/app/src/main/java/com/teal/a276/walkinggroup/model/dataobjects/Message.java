package com.teal.a276.walkinggroup.model.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Simple Message class to store the data the server expects and returns.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    private Long id;
    private Long timeStamp;
    private String text;
    private User fromUser;
    private Group toGroup;
    private boolean emergency;
    private String href;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUsers(User fromUsers) {
        this.fromUser = fromUsers;
    }

    public Group getToGroup() {
        return toGroup;
    }

    public void setToGroup(Group toGroup) {
        this.toGroup = toGroup;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return getId().equals(message.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
