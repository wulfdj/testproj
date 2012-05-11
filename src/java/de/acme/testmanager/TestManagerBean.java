package de.acme.testmanager;


import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.logging.Logger;


import de.acme.testmanager.entities.TestEntity;

@Stateless
public class TestManagerBean implements ITestManager{
	
	Logger log = Logger.getLogger(TestManagerBean.class);
	

//@PersistenceContext(type = PersistenceContextType.EXTENDED, unitName = "TestManager")
	@PersistenceContext(name="TestManager")
	EntityManager entityManager;

	@Override
	public void create(TestEntity entity) {
//		log.info("create start");
//		entity.setBean(Thread.currentThread().getName() + ", " + Thread.currentThread().getId());
		entity.setTime("" + System.currentTimeMillis());
		entityManager.persist(entity);
//		log.info("create end");
		
//		return entity;
	}

	@Override
	public void doNothing() {
		// TODO Auto-generated method stub
	}
	
	@Asynchronous
	@Override
	public Future<Integer> longProcessing() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<Integer>(42);
	}
	
//	@AroundInvoke
//	public Object log(InvocationContext ctx) throws Exception {
//	  
//		log.info("before invocation");
//		Object result = ctx.proceed();
//		log.info("after invocation");
//		return result;
//	}

}
