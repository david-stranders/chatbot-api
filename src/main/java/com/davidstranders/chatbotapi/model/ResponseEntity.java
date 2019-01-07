package com.davidstranders.chatbotapi.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEntity {

    private String speech;
    private String displayText;
    private String source;
}
