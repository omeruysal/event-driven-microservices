package com.example.productservice.core.exception;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;

import javax.annotation.Nonnull;
// we can roll back transactions between event handlers
public class ProductServiceEventsErrorHandler implements ListenerInvocationErrorHandler {
    @Override // that allows us to handle exceptions thrown in the event handler methods
    public void onError(@Nonnull Exception e, @Nonnull EventMessage<?> eventMessage, @Nonnull EventMessageHandler eventMessageHandler) throws Exception {
        throw e;
    } // we throw error to our general error handler catch it
}
