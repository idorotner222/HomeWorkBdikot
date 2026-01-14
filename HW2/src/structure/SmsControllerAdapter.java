// ============================
// SmsControllerAdapter.java
// ============================
package structure;

/**
 * Adapter that allows using the existing SMSController via the ISmsSender interface.
 * Why it exists:
 * - We keep the original SMSController class untouched.
 * - Tree depends only on ISmsSender, so we can swap implementations easily.
 */
public class SmsControllerAdapter implements ISmsSender {

    private final SMSController controller;

    public SmsControllerAdapter(SMSController controller) {
        this.controller = controller;
    }

    /**
     * Delegates to the existing SMSController.
     * Note: SMSController.sendSMS() is still not implemented,
     * but that is OK because we will not call it in unit tests.
     */
    @Override
    public void sendSMS(String message, String phone) {
        controller.sendSMS(message, phone);
    }
}
