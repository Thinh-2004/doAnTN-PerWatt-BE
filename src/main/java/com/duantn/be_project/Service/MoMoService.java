package com.duantn.be_project.Service;

import com.duantn.be_project.model.Request_Response.MoMoPaymentRequest;
import com.duantn.be_project.model.Request_Response.MoMoPaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.Base64;

@Service
public class MoMoService {

    private final RestTemplate restTemplate;

    public MoMoService() {
        this.restTemplate = new RestTemplate();
    }

    private static final String PARTNER_CODE = "MOMOLRJZ20181206";
    private static final String ACCESS_KEY = "mTCKt9W3eU1m39TW";
    private static final String SECRET_KEY = "SetA5RDnLHvt51AULf51DyauxUo3kDU6";
    private static final String REQUEST_TYPE = "payWithATM";
    private static final String NOTIFY_URL = "http://localhost:3000/order";// cancel
    private static final String RETURN_URL = "http://localhost:3000/order";// successful
    private static final String API_ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";

    public String createPayment(Long amount, String orderId, String orderInfo) throws Exception {
        String extraData = createExtraData(); // Sử dụng phương thức mã hóa extraData
        String requestId = UUID.randomUUID().toString(); // Tạo requestId duy nhất

        // Tạo rawHash với requestId duy nhất và extraData đã mã hóa
        String rawHash = "accessKey=" + ACCESS_KEY +
                "&amount=" + amount +
                "&extraData=" + extraData + // Sử dụng extraData đã mã hóa
                "&ipnUrl=" + NOTIFY_URL +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + PARTNER_CODE +
                "&redirectUrl=" + RETURN_URL +
                "&requestId=" + requestId +
                "&requestType=" + REQUEST_TYPE;

        String signature = hmacSHA256(rawHash, SECRET_KEY);

        MoMoPaymentRequest paymentRequest = new MoMoPaymentRequest();
        paymentRequest.setPartnerCode(PARTNER_CODE);
        paymentRequest.setPartnerName("MoMo");
        paymentRequest.setRequestId(requestId); // Sử dụng cùng requestId
        paymentRequest.setAmount(amount);
        paymentRequest.setOrderId(orderId);
        paymentRequest.setOrderInfo(orderInfo);
        paymentRequest.setRedirectUrl(RETURN_URL);
        paymentRequest.setIpnUrl(NOTIFY_URL);
        paymentRequest.setRequestType(REQUEST_TYPE);
        paymentRequest.setExtraData(extraData); // Sử dụng extraData đã mã hóa
        paymentRequest.setLang("vi");
paymentRequest.setSignature(signature);

        // Chuẩn bị headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo đối tượng HttpEntity chứa request body và headers
        HttpEntity<MoMoPaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);

        // Gửi yêu cầu tới MoMo
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(API_ENDPOINT, requestEntity, String.class);
        System.out.println("Response Status: " + responseEntity.getStatusCode());
        System.out.println("Response Body: " + responseEntity.getBody());

        // Xử lý phản hồi từ MoMo
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            MoMoPaymentResponse paymentResponse = objectMapper.readValue(responseEntity.getBody(),
                    MoMoPaymentResponse.class);

            if (paymentResponse.getResultCode() == 0) {
                return paymentResponse.getPayUrl(); // Trả về payUrl để sử dụng làm URL chuyển hướng
            } else {
                throw new RuntimeException("MoMo Payment failed with message: " +
                        paymentResponse.getMessage());
            }
        } else {
            throw new RuntimeException("Failed to create payment. Status Code: " +
                    responseEntity.getStatusCode());
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b)); // Định dạng từng byte thành chuỗi hex
        }
        return sb.toString();
    }

    public boolean verifySignature(String rawData, String signature) throws Exception {
        String generatedSignature = hmacSHA256(rawData, SECRET_KEY);
        return generatedSignature.equals(signature);
    }

    public String createExtraData() throws Exception {

        // Mã hóa một chuỗi JSON rỗng
        String jsonData = "{}";
        return Base64.getEncoder().encodeToString(jsonData.getBytes(StandardCharsets.UTF_8));

    }

}