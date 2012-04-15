package org.faziz.assignment.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.validation.Validator;
import org.faziz.assignment.service.SecurityService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Provides various lookup services.
 * @author faisal
 */
public final class ServiceLocator implements BeanFactoryAware{
    
    private static final Logger logger = Logger.getLogger(ServiceLocator.class.getName());
    private static BeanFactory beanFactory = null;
    
    public static void init(){
        if( beanFactory == null){
            logger.info("Initializing services...");
            beanFactory = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
    }
    
    public static EntityManagerFactory getEntityManagerFactory(){
        return (EntityManagerFactory) beanFactory.getBean("entityManagerFactory");
    }
    
    public static SecurityService getSecurityService(){
        return (SecurityService) beanFactory.getBean("securityService");
    }
    
    public static Validator getValidator(){
        return (Validator) beanFactory.getBean("validator");
    }
    
    public static PlatformTransactionManager getTransactionManager(){
        return (PlatformTransactionManager) beanFactory.getBean("transactionManager");
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        logger.log(Level.INFO, "beanFactory: {0}", beanFactory);
        ServiceLocator.beanFactory = beanFactory;
    }
}
