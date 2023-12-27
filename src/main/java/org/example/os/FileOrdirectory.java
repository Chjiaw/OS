package org.example.os;

import java.util.Date;

public class FileOrdirectory implements Cloneable {
    private int id;
    private String name;
    private int parent_id;
    private boolean is_directory;
    private int size;
    private Date created_at;
    private Date updated_at;
    private int onwer;
    private boolean opend;
    private boolean editing;
    private String content;
    private int owner_permissions;
    private int group_permissions;
    private int other_permissions;
    private int group_id;

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getOwner_permissions() {
        return owner_permissions;
    }

    public void setOwner_permissions(int owner_permissions) {
        this.owner_permissions = owner_permissions;
    }

    public int getGroup_permissions() {
        return group_permissions;
    }

    public void setGroup_permissions(int group_permissions) {
        this.group_permissions = group_permissions;
    }

    public int getOther_permissions() {
        return other_permissions;
    }

    public void setOther_permissions(int other_permissions) {
        this.other_permissions = other_permissions;
    }
    public boolean isOpend() {
        return opend;
    }

    public void setOpend(boolean opend) {
        this.opend = opend;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public boolean isIs_directory() {
        return is_directory;
    }

    public void setIs_directory(boolean is_directory) {
        this.is_directory = is_directory;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }


    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public int getOnwer() {
        return onwer;
    }

    public void setOnwer(int onwer) {
        this.onwer = onwer;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
