package dev.ktroude.ft_hangout.models;

import androidx.annotation.NonNull;

public class Message {

    private Integer id;
    private Integer contactId;
    private String msg;
    private long date;
    private boolean isSend;

    public Message(Integer id, Integer contactId, String msg, long date, boolean isSend) {
        this.id = id;
        this.contactId = contactId;
        this.msg = msg;
        this.date = date;
        this.isSend = isSend;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", contactId=" + contactId +
                ", msg='" + msg + '\'' +
                ", date=" + date +
                ", isSend=" + isSend +
                '}';
    }
}



