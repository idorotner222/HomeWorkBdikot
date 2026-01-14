// ============================
// FakeSmsSender.java (for tests)
// ============================
package structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Test Double for ISmsSender.
 * It records calls so tests can assert message/phone and number of calls.
 */
public class FakeSmsSender implements ISmsSender {

    public static class SmsCall {
        public final String message;
        public final String phone;

        public SmsCall(String message, String phone) {
            this.message = message;
            this.phone = phone;
        }
    }

    public final List<SmsCall> calls = new ArrayList<>();

    @Override
    public void sendSMS(String message, String phone) {
        calls.add(new SmsCall(message, phone));
    }
}
