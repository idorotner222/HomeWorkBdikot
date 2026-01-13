package structure;

public class SMSController {
	private String phone;
	
	public SMSController(String phone) {
		this.phone = phone;}
	
	/**
	 * send SMS with text message to the phone number phone
	 */
	public void sendSMS(String message, String phone) {
		// is not implemented yet		
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
