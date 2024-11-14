package com.duantn.be_project.model.Request_Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoMoPaymentResponse {
    String partnerCode;
    String orderId;
    String requestId;
    Long amount;
    Long responseTime;
    String message;
    int resultCode;
    String payUrl;
    String deeplink;
    String qrCodeUrl;
    String applink;
    String deeplinkMiniApp;

    // Getters and Setters
    // ...
}
