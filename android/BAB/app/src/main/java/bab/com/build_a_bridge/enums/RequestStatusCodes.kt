package bab.com.build_a_bridge.enums

/**
 * Defined values for Request object status codes
 */
enum class RequestStatusCodes {
    REQUESTED,   // requested but not accepted by volunteer
    IN_PROGRESS,
    IN_PROGRESS_REQUESTER, // accepted requests were user is requester
    IN_PROGRESS_VOLUNTEER, // accepted requests were user is volunteer
    COMPLETED,   // request completed
    INCOMPLETE   // request was not completed / cancelled
}