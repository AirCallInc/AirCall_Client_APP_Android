package com.aircall.app.Model.RequestForServices;

import java.util.ArrayList;

/**
 * Created by jd on 30/06/16.
 */
public class ServiceDetailData {
    public String Id;
    public String PurposeOfVisit;
    public String ServiceCaseNumber;
    public String ServiceRequestedOn;
    public String ServiceRequestedTime;
    public String Notes;
    public AddressDetail Address;
    public ArrayList<UnitsData> Units;
}
