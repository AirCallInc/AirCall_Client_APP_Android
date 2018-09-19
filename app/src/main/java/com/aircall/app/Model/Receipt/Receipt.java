package com.aircall.app.Model.Receipt;

import java.util.ArrayList;

/**
 * Created by jd on 05/07/16.
 */
public class Receipt {
    public String TotalAmount;
    public String FirstName;
    public String LastName;
    public String Email;
    public ArrayList<ReceiptUnitDetail> units = new ArrayList<>();
}
