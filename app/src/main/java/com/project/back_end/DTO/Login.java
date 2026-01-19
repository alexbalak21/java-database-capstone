package com.project.back_end.DTO;

public class Login {
	// The unique identifier used for login (email for Doctor/Patient, username for Admin)
	private String identifier;

	// The password provided by the user
	private String password;

	// No-args constructor for deserialization
	public Login() {}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
