package com.ecommerce.sb_ecom.security.response;

import lombok.Getter;
import lombok.Setter;

public class MessageResponse {

    @Getter
    @Setter
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
