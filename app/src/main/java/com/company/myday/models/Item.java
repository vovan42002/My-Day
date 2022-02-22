package com.company.myday.models;

public class Item {
    private Long id;
    private String name;
    private boolean selected;
    private long calendar;

    public Item(String name, long calendar) {
        this.name = name;
        this.selected = false;
        this.calendar = calendar;
    }
    public Item() { }

    public Item(String name, long calendar, boolean check) {
        this.name = name;
        this.selected = check;
        this.calendar = calendar;
    }
    public Item(String name, long calendar, boolean check, long id) {
        this.name = name;
        this.selected = check;
        this.calendar = calendar;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getCalendar() {
        return calendar;
    }

    public void setCalendar(long calendar) {
        this.calendar = calendar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", selected=" + selected +
                ", calendar=" + calendar +
                '}';
    }
}
