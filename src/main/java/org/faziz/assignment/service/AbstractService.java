package org.faziz.assignment.service;

import javax.persistence.EntityManager;

public abstract class AbstractService {
    protected EntityManager entityManager = null;
    
    public AbstractService(EntityManager entityManager){
        this.entityManager = entityManager;
    }
}
