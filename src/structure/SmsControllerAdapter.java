package structure;

public class SmsControllerAdapter implements ISmsSender {
    private final SMSController controller;

    public SmsControllerAdapter(SMSController controller) {
        this.controller = controller;
    }

    @Override
    public void sendSMS(String message, String phone) {
        controller.sendSMS(message, phone);
    }
}
