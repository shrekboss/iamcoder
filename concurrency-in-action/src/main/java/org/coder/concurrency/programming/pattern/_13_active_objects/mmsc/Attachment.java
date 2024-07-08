package org.coder.concurrency.programming.pattern._13_active_objects.mmsc;

import java.io.Serializable;

//Not pasted in the book
public class Attachment implements Serializable {

    private static final long serialVersionUID = -1604587100270138271L;

    private String contentType;
    private byte[] content = new byte[0];

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Attachment [contentType=" + contentType + ", content="
                + content.length + "]";
    }
}