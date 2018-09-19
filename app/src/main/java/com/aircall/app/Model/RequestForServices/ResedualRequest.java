package com.aircall.app.Model.RequestForServices;

import java.util.ArrayList;

/**
 * Created by jd on 29/06/16.
 */
public class ResedualRequest {
    public int ClientId = 0;
    public int AddressId = 0;
    public String PurposeOfVisit = "";
    //    public String PurposeOfVisitID = "";
    public String ServiceRequestedTime = "";
    public String ServiceRequestedOn = "";
    public String Notes = "";
    public ArrayList<Integer> Units = new ArrayList<>();

}
