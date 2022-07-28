package com.joyintech.tams.common.base.entity;

import java.util.Date;

public class FinExercisePlanDto {

    private Date exerciseDate;

    private String sn;

    public Date getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(Date exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
