package com.sunrisedentalclinic.web.api.dto;
public class TreatmentRequest {
    private String action, treatmentID, treatmentName, baseCost;
    public String getAction(){return action;} public void setAction(String v){action=v;}
    public String getTreatmentID(){return treatmentID;} public void setTreatmentID(String v){treatmentID=v;}
    public String getTreatmentName(){return treatmentName;} public void setTreatmentName(String v){treatmentName=v;}
    public String getBaseCost(){return baseCost;} public void setBaseCost(String v){baseCost=v;}
}