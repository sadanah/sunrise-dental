package com.sunrisedentalclinic.web.api.dto;
public class ReportRequest {
    private String type, startDate, endDate, dentistID;
    public String getType(){return type;} public void setType(String v){type=v;}
    public String getStartDate(){return startDate;} public void setStartDate(String v){startDate=v;}
    public String getEndDate(){return endDate;} public void setEndDate(String v){endDate=v;}
    public String getDentistID(){return dentistID;} public void setDentistID(String v){dentistID=v;}
}