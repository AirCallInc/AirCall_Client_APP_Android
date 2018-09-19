package com.aircall.app.Model.RequestForServices;

import java.util.ArrayList;

/**
 * Created by jd on 30/06/16.
 */
public class RequestForServiceListResponce {
    public String StatusCode;
    public String Message;
    public String Token;
    public ArrayList<RequestForServiceListData> Data = new ArrayList();
}
