package com.us.url_shortener.dto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ShortCodeSequenceDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public long getNext() {
        return ((Number) entityManager.createNativeQuery("SELECT nextval('short_code_seq')")
                                      .getSingleResult()).longValue();
    }
}
