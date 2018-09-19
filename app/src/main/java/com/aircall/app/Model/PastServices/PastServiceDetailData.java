package com.aircall.app.Model.PastServices;

import com.aircall.app.Model.RequestForServices.AddressDetail;

import java.util.ArrayList;

/**
 * Created by jd on 01/07/16.
 */
public class PastServiceDetailData {
    public String Id;
    public String ServiceCaseNumber;
    public String ScheduleDate;
    public String AssignedTotalTime;
    public String ScheduleStartTime;
    public String ScheduleEndTime;
    public String WorkStartedTime;
    public String WorkCompletedTime;
    public String ExtraTime;
    public Boolean IsDifferentTime;
    public String LastName;
    public String FirstName;
    public String WorkPerformed;
    public String Recommendations;
    public String EmpProfileImage;
    public String PurposeOfVisit;
    public float Rate;
    public String Review;
    public String Message;
    public Boolean IsNoShow;
    public AddressDetail Address;
    public ArrayList<PastServiceUnitDetail> Units;

}
