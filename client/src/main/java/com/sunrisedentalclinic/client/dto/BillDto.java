package com.sunrisedentalclinic.client.dto;
public class BillDto {
    private String billID, appointmentNo, consultationFee, treatmentCost, totalAmount, generatedDate;
    public String getBillID(){return billID;} public void setBillID(String v){billID=v;}
    public String getAppointmentNo(){return appointmentNo;} public void setAppointmentNo(String v){appointmentNo=v;}
    public String getConsultationFee(){return consultationFee;} public void setConsultationFee(String v){consultationFee=v;}
    public String getTreatmentCost(){return treatmentCost;} public void setTreatmentCost(String v){treatmentCost=v;}
    public String getTotalAmount(){return totalAmount;} public void setTotalAmount(String v){totalAmount=v;}
    public String getGeneratedDate(){return generatedDate;} public void setGeneratedDate(String v){generatedDate=v;}
}