package com.aircall.app.Model.NotificationListing;

import java.util.ArrayList;

/**
 * Created by jd on 19/08/16.
 */
public class NotificationListResponce {
    public String StatusCode;
    public String Token;
    public String Message;
    public String LastCallDateTime;
    public int PageNumber;
    public int PageSize;
    public int TotalNumberOfPages;
    public int TotalNumberOfRecords;
    public Boolean HasPaymentFailedUnit;
    public Boolean HasPaymentProcessingUnits;
    public ArrayList<NotificationListData> Data;
}
