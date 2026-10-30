package com.eventhosting.entity;

/**
 * Moderation state of a submitted event.
 */
public enum ApprovalStatus {

    PENDING,
    APPROVED,
    REJECTED;

    /**
     * Accepts the loose casing used by clients ("Approved", "approved", "APPROVED").
     */
    public static ApprovalStatus from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return ApprovalStatus.valueOf(value.trim().toUpperCase());
    }
}
