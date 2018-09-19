package com.aircall.app.Model.UpcomingSchedual;

import com.aircall.app.Model.RequestForServices.AddressDetail;

import java.util.ArrayList;

/**
 * Created by jd on 02/07/16.
 */
public class UpcomingServicesDetailData {
    public String Id;
    public String ScheduleYear;
    public String ScheduleMonth;
    public String MonthName;
    public String ScheduleDay;
    public String ScheduleStartTime;
    public String ScheduleEndTime;
    public String ServiceRequestedTime;
    public String PurposeOfVisit;
    public String EMPFirstName;
    public String EMPLastName;
    public String LateRescheduleDisplayMessage;
    public String EmpName;
    public String EmpProfileImage;
    public String ServiceCaseNumber;
    public String ScheduleDate;
    public String CustomerComplaints;
    public String Status;
    public String Appoinment;
    public Boolean Is24HourLeft;
    public AddressDetail Address;
    public ArrayList<UpcomingServiceUnits> Units;
    public Boolean IsRequested;
    public String LateCancelDisplayMessage;
}
