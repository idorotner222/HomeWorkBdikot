// ============================
// ISmsSender.java
// ============================
package structure;

/**
 * Abstraction for sending SMS messages.
 * Why it exists:
 * - Tree should not depend directly on SMSController (which is not implemented).
 * - In tests, we can replace this with a FakeSmsSender (Test Double) without Mockito.
 */
public interface ISmsSender {

    /**
     * Sends an SMS message to a phone number.
     * In production, an adapter will call the real SMSController.
     * In tests, a fake implementation will store the calls for assertions.
     */
    void sendSMS(String message, String phone);
}
