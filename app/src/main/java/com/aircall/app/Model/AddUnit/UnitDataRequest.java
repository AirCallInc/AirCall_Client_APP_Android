package com.aircall.app.Model.AddUnit;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */
public class UnitDataRequest {

    public String SplitType = "";
    public String ModelNumber = "";
    public String SerialNumber = "";
    public String ThermostatTypes = "";
    public int QuantityOfFilter = 0;
    public ArrayList<FilterDetailRequest> Filters = new ArrayList<>();
    public int QuantityOfFuses = 0;
    public ArrayList<FuseDetailRequest> FuseTypes = new ArrayList<>();

}
