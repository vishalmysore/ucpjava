package io.github.vishalmysore.ucp.handler;

import io.github.vishalmysore.ucp.annotation.UCPHandler;

@UCPHandler(name = "testHandler")
public class TestPaymentHandler implements PaymentHandler {

    @Override
    public PaymentHandlerResponse getHandlerDeclaration() {
        return null;
    }

    @Override
    public PaymentInstrument acquireInstrument(PaymentCredential credential, BindingContext binding) {
        return null;
    }

    @Override
    public ProcessingResult processPayment(PaymentInstrument instrument) {
        return null;
    }
}