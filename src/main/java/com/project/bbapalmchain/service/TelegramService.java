package com.project.bbapalmchain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TelegramService {

    private final String TELEGRAM_API = "https://api.telegram.org/bot";

    public void sendMessage(String message, String chatId) {
        String url = TELEGRAM_API + "8071516231:AAFzGXq1jqN85O4NAJcX4bhrgSOkFDquvRE" + "/sendMessage";

        Map<String, String> payload = new HashMap<>();
        payload.put("chat_id", chatId);
        payload.put("text", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.postForObject(url, request, String.class);
        } catch (RestClientException ex) {
            log.error("Failed to send Telegram message: {}", ex.getMessage());
        }
    }

}
