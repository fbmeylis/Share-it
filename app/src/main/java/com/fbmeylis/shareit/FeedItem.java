package com.fbmeylis.shareit;

public class FeedItem {
    private String comment;
    private String downloadurl;
    private String usermail;

    public FeedItem(String comment, String downloadurl, String usermail) {
        this.comment = comment;
        this.downloadurl = downloadurl;
        this.usermail = usermail;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getUsermail() {
        return usermail;
    }

    public void setUsermail(String usermail) {
        this.usermail = usermail;
    }
}
