package com.ffr.models;

import java.time.LocalDateTime;

public class Programme {
    private Long id;
    private LocalDateTime datePro;
    private boolean dif1;
    private boolean dif2;
    private boolean dif3;
    private boolean etat;
    private Long annonceId;
    private String annonceTitle;
    private int nbrDif;

    public Programme() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDatePro() {
        return datePro;
    }

    public void setDatePro(LocalDateTime datePro) {
        this.datePro = datePro;
    }

    public boolean isDif1() {
        return dif1;
    }

    public void setDif1(boolean dif1) {
        this.dif1 = dif1;
    }

    public boolean isDif2() {
        return dif2;
    }

    public void setDif2(boolean dif2) {
        this.dif2 = dif2;
    }

    public boolean isDif3() {
        return dif3;
    }

    public void setDif3(boolean dif3) {
        this.dif3 = dif3;
    }

    public boolean isEtat() {
        return etat;
    }

    public void setEtat(boolean etat) {
        this.etat = etat;
    }

    public Long getAnnonceId() {
        return annonceId;
    }

    public void setAnnonceId(Long annonceId) {
        this.annonceId = annonceId;
    }

    public String getAnnonceTitle() {
        return annonceTitle;
    }

    public void setAnnonceTitle(String annonceTitle) {
        this.annonceTitle = annonceTitle;
    }

    public int getNbrDif() {
        return nbrDif;
    }

    public void setNbrDif(int nbrDif) {
        this.nbrDif = nbrDif;
    }

    @Override
    public String toString() {
        return annonceTitle + " - " + datePro;
    }
}
