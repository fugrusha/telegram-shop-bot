package org.golovko.telegramshop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Slf4j
@Service
public class ValidationService {

    private static final String PHONE_NUMBER_GARBAGE_REGEX = "[()\\s-]+";
    private static final String PHONE_NUMBER_REGEX = "((\\+38)?\\(?\\d{3}\\)?[\\s.-]?(\\d{7}|\\d{3}[\\s.-]\\d{2}[\\s.-]\\d{2}|\\d{3}-\\d{4}))";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null
                && PHONE_NUMBER_PATTERN
                .matcher(phoneNumber.replaceAll(PHONE_NUMBER_GARBAGE_REGEX, "")).matches();
    }
}
