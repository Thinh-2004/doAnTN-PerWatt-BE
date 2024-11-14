package com.duantn.be_project.model.Request_Response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MoMoPaymentRequest {
    public String partnerCode;
    public String partnerName;
    public String accessKey;
    public String returnUrl;
    public String notifyUrl;
    public String subPartnerCode; 
    public String storeName;
    public String storeId; 
    public String requestId;
    public Long amount;
    public String orderId;
    public String orderInfo;
    public Long orderGroupId; 
    public String redirectUrl;
    public String ipnUrl; 
    public String requestType;
    public String extraData; 
    public String referenceId; 
    public Boolean autoCapture; 
    public String lang; 
    public String signature;

}