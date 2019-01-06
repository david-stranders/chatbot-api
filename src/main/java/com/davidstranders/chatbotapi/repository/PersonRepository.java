package com.davidstranders.chatbotapi.repository;

import com.davidstranders.chatbotapi.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
}
