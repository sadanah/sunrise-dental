package com.sunrisedentalclinic.report;

import java.time.LocalDateTime;

public abstract class Report {
    protected String reportID;
    protected LocalDateTime generatedDate;
    protected String generatedBy;

    public Report(String reportID, String generatedBy) {
        this.reportID = reportID;
        this.generatedDate = LocalDateTime.now();
        this.generatedBy = generatedBy;
    }

    public abstract void generate();

    public String getReportID() { return reportID; }
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public String getGeneratedBy() { return generatedBy; }
}