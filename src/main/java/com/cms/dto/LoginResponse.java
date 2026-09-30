package com.cms.dto;

public record LoginResponse(String token, String tokenType, String username, String role) {
}
