package com.duantn.be_project.model.Request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoMoCardPaymentRequest {
    private String partnerCode;
    private String accessKey;
    private String requestId;
    private String amount;
    private String orderId;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType;
    private String cardNumber;
    private String cardExpire;
    private String cardCvv;
    private String cardHolder;
    private String extraData;
    private String signature;
    private String lang;

    // Getters and Setters
    // ...
}
