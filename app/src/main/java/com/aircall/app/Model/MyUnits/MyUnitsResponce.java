package com.aircall.app.Model.MyUnits;

import java.util.ArrayList;

/**
 * Created by jd on 28/06/16.
 */
public class MyUnitsResponce {
    public String StatusCode;
    public String Message;
    public String Token;
    public String PageNumber;
    public Boolean HasPaymentFailedUnit;
    public Boolean HasPaymentProcessingUnits;
    public ArrayList<MyUnitsData> Data;
}
