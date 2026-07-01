package com.example.demo1.Model;

import java.util.Date;

public class Report {
    private static int nextId = 1;

    private int id;
    private int reporterId;
    private int contentId;
    private int reportedUserId;
    private ReportStatus status;
    private String description;
    private ReportType reportType;
    private Date createdDate;

    public Report(int reporterId, int contentId, int reportedUserId,
                  String description, ReportType reportType) {
        this.id = nextId++;
        this.reporterId = reporterId;
        this.contentId = contentId;
        this.reportedUserId = reportedUserId;
        this.description = description;
        this.reportType = reportType;
        this.status = ReportStatus.WAITING;
        this.createdDate = new Date();
    }
    public boolean isWaiting() {
        return status == ReportStatus.WAITING;
    }

    public int getId() {
        return id;
    }

    public int getReporterId() {
        return reporterId;
    }

    public int getContentId() {
        return contentId;
    }

    public int getReportedUserId() {
        return reportedUserId;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }
}
