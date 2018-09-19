package com.aircall.app.Model.Payment;

import java.util.ArrayList;

/**
 * Created by jd on 05/07/16.
 */
public class PaymentDetail {
    public String CardType;
    public String ClientId;
    public String Email;
    public String FirstName;
    public String LastName;
    public String Total;
    public ArrayList<PaymentUnitDetails> Units;
}
