package com.davidstranders.chatbotapi.services;

import com.davidstranders.chatbotapi.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository repository;

    @Autowired
    public RoomServiceImpl(RoomRepository repository) {
        this.repository = repository;
    }
}
