package com.fintrack.api.fintrack.domain;

public record User(long id, String email, String passwordHash) {
}
