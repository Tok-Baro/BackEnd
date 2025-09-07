package com.api.tokbaro.domain.apns.web.dto;

import lombok.Getter;

@Getter
public class StateReq {
    private String activityId;
    private double pitch;
    private double roll;
    private double yaw;
    private double ax;
    private double ay;
    private double az;
    private double rx;
    private double ry;
    private double rz;
    private double gx;
    private double gy;
    private double gz;
    private String messageTitle;
    private String messageBody;

}
