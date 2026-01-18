package io.github.vishalmysore.ucp.handler;

/**
 * Interface for UCP payment handlers.
 * Payment handlers process payment credentials into instruments and execute payments.
 */
public interface PaymentHandler {

    /**
     * Get the handler declaration with configuration and supported instruments.
     * @return Handler declaration
     */
    PaymentHandlerResponse getHandlerDeclaration();

    /**
     * Acquire a payment instrument from a payment credential.
     * @param credential The payment credential
     * @param binding The binding context
     * @return The acquired payment instrument
     */
    PaymentInstrument acquireInstrument(PaymentCredential credential, BindingContext binding);

    /**
     * Process a payment using the instrument.
     * @param instrument The payment instrument
     * @return The processing result
     */
    ProcessingResult processPayment(PaymentInstrument instrument);
}


