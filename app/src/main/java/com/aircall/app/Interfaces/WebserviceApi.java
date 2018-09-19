package com.aircall.app.Interfaces;


import com.aircall.app.Model.AboutUs.AboutUsResponse;
import com.aircall.app.Model.AddNewCard.AddNewCardResponce;
import com.aircall.app.Model.AddNewCard.GetCardByIdResponce;
import com.aircall.app.Model.AddUnit.AddUnitRequest;
import com.aircall.app.Model.AddUnit.AddUnitResponce;
import com.aircall.app.Model.BillingHistory.BillingHistoryResponce;
import com.aircall.app.Model.ChangePassword.ChangePasswordResponce;
import com.aircall.app.Model.ContactUs.AddContactUsResponce;
import com.aircall.app.Model.Dashboard.DashboardResponce;
import com.aircall.app.Model.DeleteAddress.DeleteAddressResponce;
import com.aircall.app.Model.ForgotPassword.ForgotPasswordResponce;
import com.aircall.app.Model.GetAddress.getAddressResponce;
import com.aircall.app.Model.GetAllPlanType.GetPlanTypesRateResponce;
import com.aircall.app.Model.GetAllPlanType.GetPlanTypesResponce;
import com.aircall.app.Model.GetAllState.GetAllStateResponce;
import com.aircall.app.Model.GetCityList.GetCityListResponce;
import com.aircall.app.Model.Login.LoginResponce;
import com.aircall.app.Model.Mail.SendSalesAgreementResponse;
import com.aircall.app.Model.MyUnits.MyUnitsResponce;
import com.aircall.app.Model.MyUnits.UnitsDetailResponce;
import com.aircall.app.Model.NoShowNotification.NoShowDetailsResponce;
import com.aircall.app.Model.NoShowNotification.NoShowPaymentResponce;
import com.aircall.app.Model.NotificationListing.NotificationListResponce;
import com.aircall.app.Model.Parts.GetFilterForUnitResponce;
import com.aircall.app.Model.PastServices.PastServiceDetailResponce;
import com.aircall.app.Model.PastServices.PastServiceListingResponce;
import com.aircall.app.Model.PastServices.RattingResponce;
import com.aircall.app.Model.Payment.CheckUnitPaymentResponce;
import com.aircall.app.Model.Payment.MyCartResponce;
import com.aircall.app.Model.Payment.PaymentResponce;
import com.aircall.app.Model.Payment.ValidateCreditCardResponce;
import com.aircall.app.Model.PlanCoverage.PlanCoverageDetailResponce;
import com.aircall.app.Model.PlanCoverage.PlanCoverageResponce;
import com.aircall.app.Model.RequestForServices.GetPurposeOfVisitTimeResponce;
import com.aircall.app.Model.RequestForServices.RequestForServiceListResponce;
import com.aircall.app.Model.RequestForServices.RequestServicesResponce;
import com.aircall.app.Model.RequestForServices.ResedualRequest;
import com.aircall.app.Model.RequestForServices.ServiceDeletResponce;
import com.aircall.app.Model.RequestForServices.ServiceDetailResponce;
import com.aircall.app.Model.SignUp.SignUpResponce;
import com.aircall.app.Model.UpcomingSchedual.CancelRequestedServiceResponse;
import com.aircall.app.Model.UpcomingSchedual.NotificationWaitingServicesResponce;
import com.aircall.app.Model.UpcomingSchedual.UpcomingServicesResponce;
import com.aircall.app.Model.UpdateUserProfile.ChangeUserNameResponce;
import com.aircall.app.Model.UpdateUserProfile.UpdateContactsInfoResponce;
import com.aircall.app.Model.UpdateUserProfile.UpdateUserProfileResponce;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface WebserviceApi {

    @FormUrlEncoded
    @POST("/profile/GetClientToken")
    public void getClientToken(@Field("ClientId") String ClientId, Callback<DeleteAddressResponce> callback);

    /**
     * Client Login
     */
    @FormUrlEncoded
    @POST("/profile/clientlogin")
    public void login(@Field("Email") String Email, @Field("Password") String Password,
                      @Field("DeviceType") String DeviceType, @Field("DeviceToken") String DeviceToken,
                      Callback<LoginResponce> callback);

    /**
     * Client Register
     */
    @FormUrlEncoded
    @POST("/profile/clientRegister")
    public void signUp(@Field("FirstName") String FirstName, @Field("LastName") String LastName, @Field("Company") String Company,
                       @Field("Email") String Email, @Field("Password") String Password, @Field("MobileNumber") String PhoneNumber,
                       @Field("AffiliateId") String AffiliateId, @Field("DeviceType") String DeviceType, @Field("DeviceToken") String DeviceToken,
                       Callback<SignUpResponce> callback);

    /**
     * Forgot Password
     */
    @FormUrlEncoded
    @POST("/profile/clientForgotPassword")
    public void forgotPassword(@Field("Email") String Email, Callback<ForgotPasswordResponce> callback);

    /**
     * Update unit name
     */
    @FormUrlEncoded
    @POST("/profile/UpdateClientUnit")
    public void updateClientUnit(@Field("ClientId") String ClientId, @Field("UnitId") String UnitId,
                                 @Field("UnitName") String UnitName, Callback<ForgotPasswordResponce> callback);

    /**
     * Get dashboard data
     */
    @FormUrlEncoded
    @POST("/profile/ClientDashboard")
    public void getDashboardData(@Field("ClientId") String ClientId, Callback<DashboardResponce> callback);

    /**
     * Get unit detail
     */
    @FormUrlEncoded
    @POST("/profile/GetClientUnitDetail")
    public void getClientUnitDetail(@Field("ClientId") String ClientId, @Field("UnitId") String UnitId, Callback<UnitsDetailResponce> callback);

    /**
     * Get notification list
     */
    @FormUrlEncoded
    @POST("/profile/NotificationList")
    public void notificationList(@Field("ClientId") String ClientId, @Field("PageNumber") String PageNumber, Callback<NotificationListResponce> callback);

    /**
     * Get all plan types (Ex. Residential, Multi-family etc.)
     */
    @GET("/common/getallplantype")
    public void getAllPlanType(Callback<GetPlanTypesResponce> callback);

    /**
     * Get plan wise rate
     */
    @FormUrlEncoded
    @POST("/common/GetSpecialRateByPlanType")
    public void getSpecialRateByPlanType(@Field("ClientId") String ClientId, @Field("PlanTypeId") String PlanTypeId,
                                         Callback<GetPlanTypesRateResponce> callback);

    /**
     * Get state list
     */
    @GET("/Location/GetAllState")
    public void getAllState(Callback<GetAllStateResponce> callback);

    /**
     * Get state wise city
     */
    @GET("/Location/GetAllCityByStateId")
    public void getCityByState(@Query("StateId") String StateId, Callback<GetCityListResponce> callback);

    /**
     * Get address list of client
     */
    @GET("/profile/GetClientAddress")
    public void getAllAddress(@Query("ClientId") String ClientId, Callback<getAddressResponce> callback);

    /**
     * Add new address
     */
    @FormUrlEncoded
    @POST("/profile/AddClientAddress")
    public void addClientAddress(@Field("ClientId") String ClientId, @Field("Address") String Address,
                                 @Field("State") String State,@Field("City") String City,
                                 @Field("StateName") String StateName, @Field("CityName") String CityName,
                                 @Field("ZipCode") String ZipCode, Callback<getAddressResponce> callback);

    /**
     * Add unit - DEPRECATED
     */
    @FormUrlEncoded
    @POST("/profile/AddClientUnit")
    public void addUnit(@Field("ClientId") String ClientId, @Field("UnitName") String UnitName,
                        @Field("ManufactureDate") String ManufactureDate, @Field("PlanTypeId") String PlanTypeId,
                        @Field("UnitTon") String UnitTon, @Field("AddressId") String AddressId,
                        @Field("AutoRenewal") Boolean AutoRenewal, @Field("Status") String Status,
                        @Field("UnitTypeId") String UnitTypeId, @Field("OptionalInformation") String OptionalInformation,
                        Callback<AddUnitResponce> callback);

    /**
     * Add unit
     */
    //@Field("Qty") String Qty,
    @POST("/profile/AddClientUnit")
    public void addUnitRaw(@Body AddUnitRequest addUnitRequest, Callback<AddUnitResponce> callback);


    /**
     * Get client profile info
     */
    @GET("/profile/GetClientProfile")
    public void updateUserProfile(@Query("ClientId") String ClientId, Callback<UpdateUserProfileResponce> callback);

    /**
     * Change password
     */
    @FormUrlEncoded
    @POST("/profile/clientChangePassword")
    public void changePassword(@Field("Id") String ClientId, @Field("OldPassword") String OldPassword,
                               @Field("NewPassword") String NewPassword, Callback<ChangePasswordResponce> callback);

    /**
     * Get contact info
     */
    @FormUrlEncoded
    @POST("/profile/UpdateClientContactInfo")
    public void updateConntactInfo(@Field("Id") String Id, @Field("MobileNumber") String MobileNumber,
                                   @Field("OfficeNumber") String OfficeNumber, @Field("HomeNumber") String HomeNumber,
                                   Callback<UpdateContactsInfoResponce> callback);

    /**
     * Update client information
     */
    @Multipart
    @POST("/profile/UpdateClientProfile")
    public void updateUsername(@Part("Id") String ClientId, @Part("FirstName") String FirstName,
                               @Part("LastName") String LastName, @Part("Company") String Company, @Part("Image") TypedFile Image,
                               Callback<ChangeUserNameResponce> callback);

    /**
     * Get list of part (Ex. Fuse, filter, Thermostat)
     */
    @FormUrlEncoded
    @POST("/common/GetPartsByTypeForUnit")
    public void getFilters(@Field("PartType") String PartType, Callback<GetFilterForUnitResponce> callback);


    /**
     * Remove unit from summary list
     */
    @FormUrlEncoded
    @POST("/profile/RemoveUnit")
    public void removeUnit(@Field("ClientId") String ClientId, @Field("UnitId") String UnitId,
                           Callback<AddUnitResponce> callback);

    /**
     * Get unit list for summary to renew expire plan
     */
    @FormUrlEncoded
    @POST("/profile/GetExpiredPlanUnitById")
    public void getExpiredPlanUnitById(@Field("ClientId") String ClientId, @Field("UnitId") String UnitId,
                                       @Field("NotificationId") String NotificationId, Callback<AddUnitResponce> callback);

    /**
     * Update address
     */
    @FormUrlEncoded
    @POST("/profile/UpdateClientAddress")
    public void updateClientAddress(@Field("Id") String Id, @Field("ClientId") String ClientId, @Field("Address") String Address,
                                    @Field("State") int State, @Field("City") int City,
                                    @Field("ZipCode") int ZipCode, @Field("IsDefaultAddress") Boolean IsDefaultAddress,
                                    Callback<getAddressResponce> callback);

    /**
     * Delete address
     */
    @FormUrlEncoded
    @POST("/profile/DeleteClientAddress")
    public void deleteClientAddress(@Field("AddressId") String Id, @Field("ClientId") String ClientId,
                                    Callback<DeleteAddressResponce> callback);

    /**
     * Get billing address screen detail
     */
    @FormUrlEncoded
    @POST("/profile/MyCart")
    public void myCart(@Field("ClientId") String ClientId, Callback<MyCartResponce> callback);

    /**
     * Validate card with backend from payment screen
     * Added First name,Last name,Company field and change CardType order
     */
    @FormUrlEncoded
    @POST("/profile/ValidateCreditCard")
    public void ValidateCreditCard(@Field("ClientId") String ClientId,@Field("CardType") String CardType,
                                   @Field("FirstName") String FirstName,@Field("LastName") String LastName,
                                   @Field("Company") String Company,
                                   @Field("NameOnCard") String NameOnCard, @Field("CardNumber") String CardNumber,
                                   @Field("CVV") String CVV, @Field("ExpiryMonth") String ExpiryMonth,
                                   @Field("ExpiryYear") String ExpiryYear,
                                   @Field("StripeCardId") String StripeCardId, @Field("Address") String Address,
                                   @Field("State") String State, @Field("City") String City,
                                   @Field("ZipCode") String ZipCode, @Field("PhoneNumber") String PhoneNumber,
                                   @Field("MobileNumber") String MobileNumber, Callback<ValidateCreditCardResponce> callback);

    /**
     * Make no show payment
     */
    @FormUrlEncoded
    @POST("/profile/NoShowPayment")
    public void NoShowPayment(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                              @Field("NotificationId") String NotificationId,
                              @Field("CardType") String CardType, @Field("NameOnCard") String NameOnCard,
                              @Field("CardNumber") String CardNumber, @Field("CVV") String CVV,
                              @Field("ExpiryMonth") String ExpiryMonth, @Field("ExpiryYear") String ExpiryYear,
                              @Field("Address") String Address, @Field("State") String State, @Field("City") String City,
                              @Field("ZipCode") String ZipCode, @Field("PhoneNumber") String PhoneNumber,
                              @Field("MobileNumber") String MobileNumber, Callback<NoShowPaymentResponce> callback);

    /**
     * Make renew plan payment
     */
    @FormUrlEncoded
    @POST("/profile/CollectPaymentForRenew")
    public void CollectPaymentForRenew(@Field("ClientId") String ClientId, @Field("UnitId") String UnitId,
                                       @Field("NotificationId") String NotificationId,
                                       @Field("CardType") String CardType, @Field("NameOnCard") String NameOnCard,
                                       @Field("CardNumber") String CardNumber, @Field("CVV") String CVV,
                                       @Field("ExpiryMonth") String ExpiryMonth, @Field("ExpiryYear") String ExpiryYear,
                                       @Field("CustomerPaymentProfileId") String CustomerPaymentProfileId, @Field("Address") String Address,
                                       @Field("State") String State, @Field("City") String City,
                                       @Field("ZipCode") String ZipCode, @Field("PhoneNumber") String PhoneNumber,
                                       @Field("MobileNumber") String MobileNumber, Callback<ValidateCreditCardResponce> callback);

    /**
     * make simple payment - DEPRECATED
     */
    @FormUrlEncoded
    @POST("/profile/NewMyPayment")
    public void NewMyPayment(@Field("ClientId") String ClientId, @Field("StripeCardId") String StripeCardId,
                             @Field("Address") String Address, @Field("State") String State,
                             @Field("City") String City, @Field("ZipCode") String ZipCode,
                             @Field("PhoneNumber") String PhoneNumber, @Field("MobileNumber") String MobileNumber,
                             Callback<PaymentResponce> callback);

    /**
     * Get list of plans
     */
    @FormUrlEncoded
    @POST("/profile/GetPlanListing")
    public void getPlanListing(@Field("ClientId") String ClientId, Callback<PlanCoverageResponce> callback);

    /**
     * Get plan detail
     */
    @FormUrlEncoded
    @POST("/profile/GetPlanDetail")
    public void getPlanDetail(@Field("PlanTypeId") String PlanTypeId, Callback<PlanCoverageDetailResponce> callback);

    /**
     * Get client unit list
     */
    @FormUrlEncoded
    @POST("/profile/GetClientUnit")
    public void getClientUnit(@Field("ClientId") String ClientId, @Field("PageNumber") String PageNumber,
                              @Field("AddressId") String AddressId, Callback<MyUnitsResponce> callback);

    /**
     * Get purpose of visit list and time slot - DEPRICATED
     */
    @FormUrlEncoded
    @POST("/profile/GetPurposeOfVisitTime")
    public void getPurposeOfVisitTime(@Field("ClientId") String ClientId, Callback<GetPurposeOfVisitTimeResponce> callback);

    /**
     * Get purpose of visit list and time slot
     */
    @FormUrlEncoded
    @POST("/common/GetScheduleTimeByPlanTypeServiceId")
    public void getScheduleTimeByPlanTypeServiceId(@Field("PlanTypeId") String PlanTypeId, @Field("ServiceId") String ServiceId,
                                                   @Field("RequestedServiceId") String RequestedServiceId,
                                                   Callback<GetPurposeOfVisitTimeResponce> callback);

    /**
     * Get plan type list by address
     */
    @GET("/common/GetAllPlanTypeFromAddressID")
    public void getAllPlanTypeFromAddressID(@Query("AddressId") String AddressId, Callback<GetPlanTypesResponce> callback);

    /**
     * Get client units by address and plan type
     */
    @FormUrlEncoded
    @POST("/profile/GetClientUnitByAddressIdPlanType")
    public void getClientUnitByAddressIdPlanType(@Field("ClientId") String ClientId, @Field("AddressId") String AddressId,
                                                 @Field("PlanTypeId") String PlanTypeId, Callback<MyUnitsResponce> callback);

    /**
     * request for new service
     */
    @POST("/profile/AddRequestForService")
    public void addRequestForService(@Body ResedualRequest resedualRequest, Callback<RequestServicesResponce> callback);

    /**
     * Update credit card
     */
    @FormUrlEncoded
    @POST("/profile/UpdateCreditCard")
    public void updateCreditCardDetail(@Field("Id") String Id, @Field("ClientId") String ClientId, @Field("CardType") String CardType,
                                       @Field("NameOnCard") String NameOnCard, @Field("ExpiryMonth") String ExpiryMonth,
                                       @Field("CardNumber") String CardNumber, @Field("CVV") String CVV,
                                       @Field("ExpiryYear") String ExpiryYear, @Field("IsDefaultPayment") Boolean IsDefaultPayment,
                                       Callback<AddNewCardResponce> callback);

    /**
     * Get client card list
     */
    @FormUrlEncoded
    @POST("/profile/GetCreditCard")
    public void getCreditCardList(@Field("ClientId") String ClientId, Callback<AddNewCardResponce> callback);

    /**
     * Get expired card detail
     */
    @FormUrlEncoded
    @POST("/profile/GetExpiredCreditCardById")
    public void getExpiredCreditCardById(@Field("ClientId") String ClientId, @Field("CardId") String CardId,
                                         @Field("NotificationId") String NotificationId, Callback<GetCardByIdResponce> callback);

    /**
     * Add new credit card
     */
    @FormUrlEncoded
    @POST("/profile/AddCreditCard")
    public void addNewCard(@Field("ClientId") String ClientId, @Field("CardType") String CardType,
                           @Field("NameOnCard") String NameOnCard, @Field("CardNumber") String CardNumber,
                           @Field("ExpiryMonth") String ExpiryMonth, @Field("ExpiryYear") String ExpiryYear,
                           @Field("CVV") String CVV, @Field("IsDefaultPayment") Boolean IsDefaultPayment,
                           Callback<AddNewCardResponce> callback);

    /**
     * DEPRECATED
     */
    @FormUrlEncoded
    @POST("/profile/MyPayment")
    public void makePayment(@Field("ClientId") String ClientId, @Field("Address") String Address,
                            @Field("State") String State, @Field("City") String City,
                            @Field("ZipCode") String ZipCode, @Field("CardType") String CardType,
                            @Field("NameOnCard") String NameOnCard, @Field("CardNumber") String CardNumber,
                            @Field("CVV") String CVV, @Field("ExpiryMonth") String ExpiryMonth,
                            @Field("ExpiryYear") String ExpiryYear, @Field("PhoneNumber") String PhoneNumber,
                            @Field("MobileNumber") String MobileNumber, Callback<PaymentResponce> callback);

    /**
     * Get billing history list
     */
    @FormUrlEncoded
    @POST("/profile/GetBillingHistory")
    public void getBillingHistorylist(@Field("ClientId") String ClientId, Callback<BillingHistoryResponce> callback);

    /**
     * Get billing history's detail
     */
    @FormUrlEncoded
    @POST("/profile/GetBillingHistoryDetail")
    public void getBillingHistoryDetail(@Field("ClientId") String ClientId, @Field("BillingId") String BillingId,
                                        Callback<BillingHistoryResponce> callback);


    /**
     * Get list of requested service
     */
    @FormUrlEncoded
    @POST("/profile/RequestForServiceList")
    public void requestForServiceList(@Field("ClientId") String ClientId, Callback<RequestForServiceListResponce> callback);

    /**
     * Get detail of requested service
     */
    @FormUrlEncoded
    @POST("/profile/RequestForServiceDetail")
    public void requestForServiceDetail(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                                        Callback<ServiceDetailResponce> callback);

    /**
     * Delete any requested service
     */
    @FormUrlEncoded
    @POST("/profile/DeleteRequestedService")
    public void deleteRequestedService(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                                       Callback<ServiceDeletResponce> callback);

    /**
     * Update any requested service
     */
    @FormUrlEncoded
    @POST("/profile/EditRequestForService")
    public void editRequestForService(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                                      @Field("ServiceRequestedTime") String ServiceRequestedTime,
                                      @Field("ServiceRequestedOn") String ServiceRequestedOn, @Field("Reason") String Reason,
                                      Callback<RequestForServiceListResponce> callback);

    /**
     * Get past service listing
     */
    @FormUrlEncoded
    @POST("/profile/PastServiceListing")
    public void pastServiceListing(@Field("ClientId") String ClientId, @Field("PageNumber") String PageNumber,
                                   Callback<PastServiceListingResponce> callback);

    /**
     * Get past service detail
     */
    @FormUrlEncoded
    @POST("/profile/PastServiceListingDetail")
    public void PastServiceListingDetail(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                                         @Field("NotificationId") String NotificationId, Callback<PastServiceDetailResponce> callback);

    /**
     * Submit ratting and review for past services
     */
    @FormUrlEncoded
    @POST("/profile/ServiceRating")
    public void serviceRating(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                              @Field("Rate") float Rate, @Field("Review") String Review,
                              Callback<RattingResponce> callback);

    /**
     * Get list of upcoming service
     */
    @FormUrlEncoded
    @POST("/profile/UpcomingServices")
    public void upcomingServices(@Field("ClientId") String ClientId, Callback<UpcomingServicesResponce> callback);

    /**
     * Get detail of service
     */
    @FormUrlEncoded
    @POST("/profile/WaitingServiceDetail")
    public void WaitingServiceDetail(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                                     @Field("NotificationId") String NotificationId,
                                     Callback<NotificationWaitingServicesResponce> callback);

    /**
     * Give approval to service schedule
     */
    @FormUrlEncoded
    @POST("/profile/ApproveWaitingService")
    public void ApproveWaitingService(@Field("ClientId") String ClientId, @Field("NotificationId") String NotificationId,
                                      @Field("ServiceId") String ServiceId, Callback<RattingResponce> callback);

    /**
     * Request to reschedule service
     */
    @FormUrlEncoded
    @POST("/profile/ServiceRecheduleRequest")
    public void serviceRecheduleRequest(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                                        @Field("NotificationId") String NotificationId,
                                        @Field("ServiceRequestedTime") String ServiceRequestedTime,
                                        @Field("ServiceRequestedOn") String ServiceRequestedOn, @Field("Reason") String Reason,
                                        @Field("IsCancelled") Boolean IsCancelled,
                                        @Field("IsLateReschedule") Boolean IsLateReschedule,
                                        Callback<UpcomingServicesResponce> callback);

    /**
     * Request to Cancel service
     */
    @FormUrlEncoded
    @POST("/profile/CancelRequestedServices")
    public void serviceCancelRequest(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                                     @Field("NotificationId") String NotificationId,
                                     @Field("ServiceRequestedTime") String ServiceRequestedTime,
                                     @Field("ServiceRequestedOn") String ServiceRequestedOn, @Field("Reason") String Reason,
                                     @Field("IsCancelled") Boolean IsCancelled,
                                     @Field("IsLateReschedule") Boolean IsLateReschedule,
                                     Callback<CancelRequestedServiceResponse> callback);


    /**
     * Submit contact us
     */
    @FormUrlEncoded
    @POST("/profile/ClientContactUs")
    public void addContactUsDetail(@Field("Name") String Name,
                                   @Field("Email") String Email, @Field("PhoneNumber") String PhoneNumber,
                                   @Field("Message") String Message, Callback<AddContactUsResponce> callback);

    /**
     * Get CMS pages (Ex. AboutUs, Terms etc.)
     */
    @FormUrlEncoded
    @POST("/common/GetCMSPages")
    public void getAboutUsInformation(@Field("PageId") String PageId, Callback<AboutUsResponse> callback);

    /**
     * Get units of failed payments
     */
    @FormUrlEncoded
    @POST("/profile/GetPaymentFailedUnit")
    public void getPaymentFailedUnit(@Field("ClientId") String ClientId, Callback<AddUnitResponce> callback);

    /**
     * Check payment status of client
     */
    @FormUrlEncoded
    @POST("/profile/CheckMyPaymentStatus")
    public void checkMyPaymentStatus(@Field("ClientId") String ClientId, @Field("UnitId") String UnitId,
                                     Callback<CheckUnitPaymentResponce> callback);

    /**
     * Delete old added unpaid units from cart.
     */
    @FormUrlEncoded
    @POST("/profile/DeleteOldData")
    public void deleteOldData(@Field("ClientId") String ClientId, Callback<RattingResponce> callback);

    /**
     * Client logout
     */
    @FormUrlEncoded
    @POST("/profile/clientlogout")
    public void Clientlogout(@Field("ClientId") String ClientId, Callback<RattingResponce> callback);

    /**
     * Get details of NoShow Notification
     */
    @FormUrlEncoded
    @POST("/profile/NoShowServiceDetails")
    public void noShowServiceDetails(@Field("ClientId") String ClientId, @Field("ServiceId") String ServiceId,
                                     @Field("NotificationId") String NotificationId, Callback<NoShowDetailsResponce> callback);

    //Added newly
    @GET("/common/SendSalesAgreement")
    public void sendSalesAgreement(@Query("ClientId") String ClientId, Callback<SendSalesAgreementResponse> callback);

}
