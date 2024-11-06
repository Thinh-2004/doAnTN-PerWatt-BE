package com.duantn.be_project.model.Request_Response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
public class MoMoPaymentRequest {
    public String partnerCode;
    public String accessKey;
    public String returnUrl;
    public String notifyUrl;
    public String subPartnerCode; // Optional
    public String storeName; // Optional
    public String storeId; // Optional
    public String requestId;
    public Long amount;
    public String orderId;
    public String orderInfo;
    public Long orderGroupId; // Optional
    public String redirectUrl; // Optional
    public String ipnUrl; // Optional
    public String requestType;
    public String extraData; // Optional
    public String referenceId; // Optional
    public Boolean autoCapture; // Optional
    public String lang; // Optional
    public String signature;

}
