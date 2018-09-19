package com.aircall.app.Model.RequestForServices;

import java.util.ArrayList;

/**
 * Created by jd on 30/06/16.
 */
public class RequestForServiceListData {
    public String Id;
    public String ServiceCaseNumber;
    public String ServiceRequestedTime;
    public String ServiceRequestedOn;
    public String Message;
    public String UnitCount;
    public String PurposeOfVisit;
//    public String Address;
//    Changed Boolean tp boolean
    public boolean AllowDelete;
    public String Address;
    public ArrayList<RequestForServiceListService> Services = new ArrayList();

}
