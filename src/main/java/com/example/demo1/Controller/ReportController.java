package com.example.demo1.Controller;

import com.example.demo1.Model.Database;
import com.example.demo1.Model.Report;
import com.example.demo1.Model.ReportStatus;
import com.example.demo1.Model.ReportType;

import java.util.List;

public class ReportController {
    private Database db = Database.getInstance();

    public String createReport(int reporterId, int contentId, int reportedUserId, String description, ReportType reportType) {
        if (description == null || description.trim().isEmpty()) return "description required";
        Report report = new Report(reporterId, contentId, reportedUserId, description, reportType);
        db.addReport(report);
        return "report submitted";
    }

    public List<Report> getAllReports() {
        return db.getAllReports();
    }

    public List<Report> getWaitingReports() {
        return db.getReportsByStatus(ReportStatus.WAITING);
    }
}
