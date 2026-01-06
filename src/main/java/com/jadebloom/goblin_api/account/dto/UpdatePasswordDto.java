package com.jadebloom.goblin_api.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jadebloom.goblin_api.security.validation.ValidUserPassword;

public class UpdatePasswordDto {

	@ValidUserPassword(
			message = "Old password must be 8 - 32 latin characters long and contain at least one uppercase letter, one lowercase letter, number and special symbol ('!', '?', '_', '$', '#', '%', '^', '&', '*'")
	@JsonProperty("old_password")
	private String oldPassword;


	@ValidUserPassword(
			message = "New password must be 8 - 32 latin characters long and contain at least one uppercase letter, one lowercase letter, number and special symbol ('!', '?', '_', '$', '#', '%', '^', '&', '*'")
	@JsonProperty("new_password")
	private String newPassword;

	public UpdatePasswordDto(String oldPassword, String newPassword) {
		this.oldPassword = oldPassword;

		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	@Override
	public String toString() {
		return "UpdatePasswordDto(oldPassword=" + oldPassword + ", newPassword=" + newPassword
				+ ")";
	}

}
