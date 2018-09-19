package com.aircall.app.Model.RequestForServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jd on 29/06/16.
 */
public class GetPurposeOfVisitTimeResponceData {
    public String TimeSlot1;
    public String TimeSlot2;
    public String EmergencyServiceSlot1;
    public String EmergencyServiceSlot2;
    public int MaintenanceServicesWithinDays;
    public int EmergencyAndOtherServiceWithinDays;
    public int TotalUnitSlot1;
    public int TotalUnitSlot2;

//    public ArrayList<String> Purpose;

    public List<PurposeData> Purpose = new ArrayList<>();

    public class PurposeData {
        public String Id;
        public String Name;
        public String Message;
    }
}
