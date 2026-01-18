package io.github.vishalmysore.ucp.domain.checkout;

public enum CheckoutStatus {
    incomplete,
    requires_escalation,
    ready_for_complete,
    complete_in_progress,
    completed,
    canceled
}

