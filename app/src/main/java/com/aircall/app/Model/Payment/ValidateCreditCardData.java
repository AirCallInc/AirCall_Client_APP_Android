package com.aircall.app.Model.Payment;

import java.util.ArrayList;

/**
 * Created by Jd on 02/07/16.
 */
public class ValidateCreditCardData {
    public String StripeCardId;
    public String CardType;
    public String ClientId;
    public String Email;
    public String FirstName;
    public String LastName;
    public String Total;
    public ArrayList<PaymentUnitDetails> Units;
}
