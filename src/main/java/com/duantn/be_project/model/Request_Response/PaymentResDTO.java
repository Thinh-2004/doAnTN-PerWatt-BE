package com.duantn.be_project.model.Request_Response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResDTO implements Serializable {
    private String status;
    private String message;
    private String URL;
}
