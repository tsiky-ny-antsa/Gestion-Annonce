package com.ffr.models;

import java.time.LocalDateTime;

public class Annonce {
    private Long id;
    private String title;
    private String content;
    private Long categoryId;
    private String categoryName;
    private String audioPath;
    private String prop;
    private String type;
    private int nbrDif;
    private int nbrPrev;
    private Long createdBy;
    private String createdByUsername;
    private LocalDateTime dateCre;
    private LocalDateTime dateUpd;

    public Annonce() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNbrDif() {
        return nbrDif;
    }

    public void setNbrDif(int nbrDif) {
        this.nbrDif = nbrDif;
    }

    public int getNbrPrev() {
        return nbrPrev;
    }

    public void setNbrPrev(int nbrPrev) {
        this.nbrPrev = nbrPrev;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public LocalDateTime getDateCre() {
        return dateCre;
    }

    public void setDateCre(LocalDateTime dateCre) {
        this.dateCre = dateCre;
    }

    public LocalDateTime getDateUpd() {
        return dateUpd;
    }

    public void setDateUpd(LocalDateTime dateUpd) {
        this.dateUpd = dateUpd;
    }

    @Override
    public String toString() {
        return title;
    }
}
