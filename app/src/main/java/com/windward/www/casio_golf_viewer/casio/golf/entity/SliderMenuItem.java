package com.windward.www.casio_golf_viewer.casio.golf.entity;

/**
 * 侧滑菜单的每个Item
 */
public class SliderMenuItem {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public SliderMenuItem(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "SliderMenu{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
