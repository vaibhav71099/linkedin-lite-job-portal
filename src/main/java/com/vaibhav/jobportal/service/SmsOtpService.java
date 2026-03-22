package com.vaibhav.jobportal.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.vaibhav.jobportal.exception.OtpDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsOtpService {

	private final String accountSid;
	private final String authToken;
	private final String fromNumber;

	public SmsOtpService(
		@Value("${twilio.account-sid:}") String accountSid,
		@Value("${twilio.auth-token:}") String authToken,
		@Value("${twilio.from-number:}") String fromNumber
	) {
		this.accountSid = accountSid;
		this.authToken = authToken;
		this.fromNumber = fromNumber;
	}

	public void sendOtp(String phone, String otp) {
		if (!isConfigured()) {
			throw new OtpDeliveryException("SMS service is not configured.");
		}

		Twilio.init(accountSid, authToken);
		Message.creator(
			new PhoneNumber(phone),
			new PhoneNumber(fromNumber),
			"Your OTP for Job Portal is: " + otp + ". It expires soon."
		).create();
	}

	public boolean isConfigured() {
		return accountSid != null && !accountSid.isBlank()
			&& authToken != null && !authToken.isBlank()
			&& fromNumber != null && !fromNumber.isBlank();
	}
}
