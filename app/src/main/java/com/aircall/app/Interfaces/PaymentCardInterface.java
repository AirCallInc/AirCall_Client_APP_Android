package com.aircall.app.Interfaces;

/**
 * Created by kartik on 30/06/16.
 */
public interface PaymentCardInterface {

    void cardDetail(int position, String cardHolderName, String cardNumber, String cardType, String exMonth, String exYear, boolean isSelect);

}
