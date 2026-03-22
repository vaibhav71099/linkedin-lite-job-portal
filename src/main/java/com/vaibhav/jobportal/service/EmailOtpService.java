package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.exception.OtpDeliveryException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailOtpService {

	private final ObjectProvider<JavaMailSender> mailSenderProvider;
	private final String fromAddress;
	private final String mailHost;

	public EmailOtpService(
		ObjectProvider<JavaMailSender> mailSenderProvider,
		@Value("${spring.mail.host:}") String mailHost,
		@Value("${app.mail.from:}") String fromAddress
	) {
		this.mailSenderProvider = mailSenderProvider;
		this.mailHost = mailHost;
		this.fromAddress = fromAddress;
	}

	public void sendOtp(String email, String otp) {
		JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
		if (mailSender == null || mailHost == null || mailHost.isBlank()
			|| fromAddress == null || fromAddress.isBlank()) {
			throw new OtpDeliveryException("Email service is not configured.");
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setFrom(fromAddress);
		message.setSubject("Your OTP for Job Portal");
		message.setText("Your email OTP is: " + otp + ". It expires soon.");
		try {
			mailSender.send(message);
		} catch (MailException ex) {
			throw new OtpDeliveryException("Email OTP could not be sent. Check SMTP configuration and try again.");
		}
	}
}
