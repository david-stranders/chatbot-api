package com.davidstranders.chatbotapi.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DialogFlowResponseEntity {

    private String fulfillmentText;
    private String source;
}
