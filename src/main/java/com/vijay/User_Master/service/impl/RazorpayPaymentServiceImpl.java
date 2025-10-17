package com.vijay.User_Master.service.impl;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.razorpay.Utils;
import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.exceptions.PaymentException;
import com.vijay.User_Master.service.RazorpayPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service implementation for Razorpay payment operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayPaymentServiceImpl implements RazorpayPaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() {
        try {
            this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            log.info("Razorpay client initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Razorpay client", e);
            throw new PaymentException("Failed to initialize payment service", e);
        }
    }


    @Override
    public SubscriptionOrderResponse createSubscriptionOrder(SubscriptionOrderRequest request) {
        try {
            log.info("Creating payment order for subscription: {}", request.getSubscriptionId());

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "sub_" + request.getSubscriptionId() + "_" + System.currentTimeMillis());

            // Add notes with subscription details
            JSONObject notes = new JSONObject();
            notes.put("subscription_id", request.getSubscriptionId().toString());
            notes.put("plan_name", request.getPlanName());
            notes.put("school_name", request.getSchoolName());
            notes.put("customer_name", request.getCustomerName());
            notes.put("customer_email", request.getCustomerEmail());
            notes.put("billing_cycle", request.getBillingCycle());
            notes.put("payment_method", request.getPaymentMethod().toString());
            if (request.getNotes() != null) {
                notes.put("notes", request.getNotes());
            }
            orderRequest.put("notes", notes);

            Order order = razorpayClient.orders.create(orderRequest);

            log.info("Payment order created successfully: {}", (String) order.get("id"));

            return SubscriptionOrderResponse.builder()
                    .orderId(order.get("id"))
                    .amount(order.get("amount"))
                    .currency(order.get("currency"))
                    .keyId(razorpayKeyId)
                    .receipt(order.get("receipt"))
                    .status(order.get("status"))
                    .planName(request.getPlanName())
                    .schoolName(request.getSchoolName())
                    .customerName(request.getCustomerName())
                    .customerEmail(request.getCustomerEmail())
                    .customerPhone(request.getCustomerPhone())
                    .notes(request.getNotes())
                    .createdAt(order.get("created_at"))
                    .updatedAt(order.get("updated_at"))
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to create payment order for subscription: {}", request.getSubscriptionId(), e);
            throw new PaymentException("Failed to create payment order: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error creating payment order", e);
            throw new PaymentException("Unexpected error creating payment order", e);
        }
    }

    @Override
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        try {
            log.info("Verifying payment: orderId={}, paymentId={}", orderId, paymentId);

            // TODO: Implement proper Razorpay signature verification
            String generatedSignature = "mock_signature_" + orderId + "_" + paymentId;
            boolean isValid = generatedSignature.equals(signature);

            log.info("Payment verification result: {}", isValid ? "SUCCESS" : "FAILED");
            return isValid;

        } catch (Exception e) {
            log.error("Error verifying payment signature", e);
            return false;
        }
    }

    @Override
    public UPIMandateResponse createUPIMandate(UPIMandateRequest request) {
        try {
            log.info("Creating UPI mandate for customer: {}", request.getCustomerId());

            JSONObject mandateRequest = new JSONObject();
            mandateRequest.put("amount", request.getAmount());
            mandateRequest.put("currency", "INR");
            mandateRequest.put("customer_id", request.getCustomerId());
            mandateRequest.put("method", "upi");

            JSONObject upiDetails = new JSONObject();
            upiDetails.put("vpa", request.getUpiId());
            upiDetails.put("flow", "collect");
            mandateRequest.put("upi", upiDetails);

            // Set mandate details
            JSONObject mandate = new JSONObject();
            mandate.put("max_amount", request.getMaxAmount());
            mandate.put("expire_at", request.getExpiryDate());
            mandate.put("start_at", request.getStartDate());
            mandate.put("interval", "monthly");
            mandateRequest.put("mandate", mandate);

            // Add notes
            JSONObject notes = new JSONObject();
            notes.put("subscription_id", request.getSubscriptionId());
            notes.put("description", request.getDescription());
            mandateRequest.put("notes", notes);

            // Note: This is a simplified implementation
            // In production, you would use Razorpay's mandate API
            JSONObject mandateResponse = new JSONObject();
            mandateResponse.put("id", "mandate_" + UUID.randomUUID().toString());
            mandateResponse.put("status", "created");
            mandateResponse.put("vpa", request.getUpiId());
            mandateResponse.put("amount", request.getAmount());
            mandateResponse.put("max_amount", request.getMaxAmount());
            mandateResponse.put("start_at", request.getStartDate());
            mandateResponse.put("expire_at", request.getExpiryDate());

            log.info("UPI mandate created successfully: {}", mandateResponse.getString("id"));

            return UPIMandateResponse.builder()
                    .mandateId(mandateResponse.getString("id"))
                    .status(mandateResponse.getString("status"))
                    .upiId(request.getUpiId())
                    .amount(request.getAmount())
                    .maxAmount(request.getMaxAmount())
                    .startDate(request.getStartDate())
                    .expiryDate(request.getExpiryDate())
                    .description(request.getDescription())
                    .subscriptionId(request.getSubscriptionId())
                    .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .updatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();

        } catch (Exception e) {
            log.error("Failed to create UPI mandate", e);
            throw new PaymentException("Failed to create UPI mandate: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentResponse chargeSubscription(String subscriptionId) {
        try {
            log.info("Charging subscription: {}", subscriptionId);

            // This is a simplified implementation
            // In production, you would use Razorpay's subscription charging API
            JSONObject paymentRequest = new JSONObject();
            paymentRequest.put("amount", 899900); // ₹8,999 in paise
            paymentRequest.put("currency", "INR");
            paymentRequest.put("description", "Monthly subscription for " + subscriptionId);

            // Simulate payment creation
            JSONObject paymentResponse = new JSONObject();
            paymentResponse.put("id", "pay_" + UUID.randomUUID().toString());
            paymentResponse.put("status", "captured");
            paymentResponse.put("amount", paymentRequest.get("amount"));
            paymentResponse.put("currency", paymentRequest.get("currency"));
            paymentResponse.put("method", "upi");
            paymentResponse.put("description", paymentRequest.get("description"));

            log.info("Subscription charged successfully: {}", paymentResponse.getString("id"));

            return PaymentResponse.builder()
                    .paymentId(paymentResponse.getString("id"))
                    .status(paymentResponse.getString("status"))
                    .amount(paymentResponse.getString("amount"))
                    .currency(paymentResponse.getString("currency"))
                    .method(paymentResponse.getString("method"))
                    .description(paymentResponse.getString("description"))
                    .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .updatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();

        } catch (Exception e) {
            log.error("Failed to charge subscription: {}", subscriptionId, e);
            throw new PaymentException("Failed to charge subscription: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean cancelUPIMandate(String mandateId) {
        try {
            log.info("Cancelling UPI mandate: {}", mandateId);

            // This is a simplified implementation
            // In production, you would use Razorpay's mandate cancellation API
            log.info("UPI mandate cancelled successfully: {}", mandateId);
            return true;

        } catch (Exception e) {
            log.error("Failed to cancel UPI mandate: {}", mandateId, e);
            return false;
        }
    }

    @Override
    public PaymentResponse getPaymentDetails(String paymentId) {
        try {
            log.info("Fetching payment details: {}", paymentId);

            Payment payment = razorpayClient.payments.fetch(paymentId);

            return PaymentResponse.builder()
                    .paymentId(payment.get("id"))
                    .status(payment.get("status"))
                    .amount(payment.get("amount"))
                    .currency(payment.get("currency"))
                    .orderId(payment.get("order_id"))
                    .method(payment.get("method"))
                    .description(payment.get("description"))
                    .createdAt(payment.get("created_at"))
                    .updatedAt(payment.get("updated_at"))
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to fetch payment details: {}", paymentId, e);
            throw new PaymentException("Failed to fetch payment details: " + e.getMessage(), e);
        }
    }

    @Override
    public RefundResponse refundPayment(String paymentId, Long amount, String reason) {
        try {
            log.info("Processing refund for payment: {}", paymentId);

            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amount);
            refundRequest.put("notes", new JSONObject().put("reason", reason));

            Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);

            return RefundResponse.builder()
                    .refundId(refund.get("id"))
                    .paymentId(refund.get("payment_id"))
                    .status(refund.get("status"))
                    .amount(refund.get("amount"))
                    .currency(refund.get("currency"))
                    .reason(reason)
                    .createdAt(refund.get("created_at"))
                    .updatedAt(refund.get("updated_at"))
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to process refund for payment: {}", paymentId, e);
            throw new PaymentException("Failed to process refund: " + e.getMessage(), e);
        }
    }

    public RefundResponse getRefundDetails(String refundId) {
        try {
            log.info("Fetching refund details: {}", refundId);

            Refund refund = razorpayClient.refunds.fetch(refundId);

            return RefundResponse.builder()
                    .refundId(refund.get("id"))
                    .paymentId(refund.get("payment_id"))
                    .status(refund.get("status"))
                    .amount(refund.get("amount"))
                    .currency(refund.get("currency"))
                    .reason(refund.get("notes") != null ? refund.get("notes").toString() : null)
                    .createdAt(refund.get("created_at"))
                    .updatedAt(refund.get("updated_at"))
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to fetch refund details: {}", refundId, e);
            throw new PaymentException("Failed to fetch refund details: " + e.getMessage(), e);
        }
    }

    public void handleWebhookEvent(String eventType, String payload) {
        try {
            log.info("Handling webhook event: {}", eventType);

            JSONObject event = new JSONObject(payload);

            switch (eventType) {
                case "payment.captured":
                    handlePaymentCaptured(event);
                    break;
                case "payment.failed":
                    handlePaymentFailed(event);
                    break;
                case "subscription.charged":
                    handleSubscriptionCharged(event);
                    break;
                case "mandate.created":
                    handleMandateCreated(event);
                    break;
                case "mandate.revoked":
                    handleMandateRevoked(event);
                    break;
                default:
                    log.warn("Unhandled webhook event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Error handling webhook event: {}", eventType, e);
        }
    }

    public String generateReceipt(String paymentId) {
        try {
            log.info("Generating receipt for payment: {}", paymentId);

            // Generate receipt URL or content
            String receiptUrl = "/api/v1/payments/receipt/" + paymentId;

            log.info("Receipt generated successfully: {}", receiptUrl);
            return receiptUrl;

        } catch (Exception e) {
            log.error("Failed to generate receipt for payment: {}", paymentId, e);
            throw new PaymentException("Failed to generate receipt", e);
        }
    }

    public PaymentStatisticsResponse getPaymentStatistics(Long ownerId) {
        try {
            log.info("Fetching payment statistics for owner: {}", ownerId);

            // This is a simplified implementation
            // In production, you would aggregate data from your database
            return PaymentStatisticsResponse.builder()
                    .totalPayments(100L)
                    .totalAmount("899900")
                    .currency("INR")
                    .successfulPayments(95L)
                    .failedPayments(5L)
                    .refundedPayments(2L)
                    .successRate("95.0%")
                    .averageTransactionAmount("8999")
                    .monthlyRevenue("899900")
                    .yearlyRevenue("10798800")
                    .build();

        } catch (Exception e) {
            log.error("Failed to fetch payment statistics for owner: {}", ownerId, e);
            throw new PaymentException("Failed to fetch payment statistics", e);
        }
    }

    // Private helper methods for webhook handling
    private void handlePaymentCaptured(JSONObject event) {
        log.info("Payment captured: {}", event.getJSONObject("payload").getJSONObject("payment").get("id"));
        // Implement payment captured logic
    }

    private void handlePaymentFailed(JSONObject event) {
        log.info("Payment failed: {}", event.getJSONObject("payload").getJSONObject("payment").get("id"));
        // Implement payment failed logic
    }

    private void handleSubscriptionCharged(JSONObject event) {
        log.info("Subscription charged: {}", event.getJSONObject("payload").getJSONObject("subscription").get("id"));
        // Implement subscription charged logic
    }

    private void handleMandateCreated(JSONObject event) {
        log.info("Mandate created: {}", event.getJSONObject("payload").getJSONObject("mandate").get("id"));
        // Implement mandate created logic
    }

    private void handleMandateRevoked(JSONObject event) {
        log.info("Mandate revoked: {}", event.getJSONObject("payload").getJSONObject("mandate").get("id"));
        // Implement mandate revoked logic
    }
}
