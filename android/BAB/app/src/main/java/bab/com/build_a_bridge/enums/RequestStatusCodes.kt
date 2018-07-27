package bab.com.build_a_bridge.enums

enum class RequestStatusCodes {
    REQUESTED,   // requested but not accepted by volunteer
    IN_PROGRESS, // accepted by volunteer
    COMPLETED,   // request completed
    INCOMPLETE   // request was not completed / cancelled
}