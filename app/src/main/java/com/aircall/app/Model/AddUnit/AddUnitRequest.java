package com.aircall.app.Model.AddUnit;

import java.math.BigDecimal;

/**
 * Created by jd on 20/06/16.
 */
public class AddUnitRequest {
    public String ClientId;
    public String UnitName = "";
    public String ManufactureDate = "";
    public String PlanTypeId = "";
    public BigDecimal PricePerMonth;
    public int VisitPerYear = 1;
    public String UnitTon = "";
    public String AddressId = "";
    public Boolean AutoRenewal = false;
    public Boolean SpecialOffer = false;
    public String Status = "1";
    public String UnitTypeId = "1";
    public String Qty;
//    public ArrayList<UnitDataRequest> OptionalInformation = new ArrayList<>();
}
