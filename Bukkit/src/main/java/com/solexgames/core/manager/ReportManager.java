package com.solexgames.core.manager;

import com.solexgames.core.player.report.Report;
import lombok.Getter;

import java.util.ArrayList;

public class ReportManager {

    @Getter
    private final ArrayList<Report> reports = new ArrayList<>();

    public void registerReport(Report report) {
        this.reports.add(report);
    }
}
