package com.example.userservice.query;

import com.example.core.commands.data.PaymentDetails;
import com.example.core.commands.data.User;
import com.example.core.commands.query.FetchUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserEventsHandler {

    @QueryHandler
    public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query){
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("Omer Uysal")
                .validUntilYear(12)
                .validUntilYear(2030)
                .build();
        User user = User.builder()
                .firstName("Omer")
                .lastName("Uysal")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();
        return user;
    }
}
