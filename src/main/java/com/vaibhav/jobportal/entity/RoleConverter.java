package com.vaibhav.jobportal.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RoleConverter implements AttributeConverter<Role, String> {

	@Override
	public String convertToDatabaseColumn(Role role) {
		return role == null ? null : role.name();
	}

	@Override
	public Role convertToEntityAttribute(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		String normalized = value.startsWith("ROLE_") ? value.substring(5) : value;
		return Role.valueOf(normalized);
	}
}
