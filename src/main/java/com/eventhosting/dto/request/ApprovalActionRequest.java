package com.eventhosting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ApprovalActionRequest {

    /**
     * "Approved" or "Rejected" (case-insensitive).
     */
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "(?i)approved|rejected",
             message = "Status must be either 'Approved' or 'Rejected'")
    private String status;

    /**
     * Required when the status is "Rejected"; checked in ApprovalService.
     */
    private String rejectionReason;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
