package com.aircall.app.Model.Dashboard;

import java.util.ArrayList;

/**
 * Created by jd on 17/06/16.
 */
public class DashboardData {

    public String FirstName;
    public String LastName;
    public String PhoneNumber;
    public String DefaultAddress;
    public Boolean HasPaymentFailedUnit;
    public Boolean HasPaymentProcessingUnits;
    public int NotificationCount;
    public ArrayList<UnitDetails> Units;
    public ArrayList<NotificationDetail> Notifications;
}
