package com.liveshield.service;

public record PriorityAction(
        String priority,
        String title,
        String description,
        int riskPoints) {
}