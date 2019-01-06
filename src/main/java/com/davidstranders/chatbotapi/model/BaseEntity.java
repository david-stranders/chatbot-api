package com.davidstranders.chatbotapi.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
