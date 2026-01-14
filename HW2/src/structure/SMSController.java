package structure;

/**
 * SMSController is responsible for sending SMS messages.
 *
 * IMPORTANT:
 * The sendSMS method is NOT implemented.
 * This is intentional in the homework.
 *
 * Because of this, Tree MUST NOT depend directly on this class
 * during unit testing.
 */
public class SMSController {

    private String phone;

    /**
     * Constructor that stores a phone number.
     */
    public SMSController(String phone) {
        this.phone = phone;
    }

    /**
     * Sends an SMS message to a phone number.
     * Currently NOT implemented.
     *
     * In production: this would send a real SMS.
     * In unit tests: this method must be replaced
     * by a Test Double (FakeSmsSender).
     */
    public void sendSMS(String message, String phone) {
        // not implemented on purpose
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
