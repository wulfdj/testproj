package de.acme.testmanager;

import java.util.concurrent.Future;

import javax.ejb.Remote;

import de.acme.testmanager.entities.TestEntity;

@Remote
public interface ITestManager {
	public void create(TestEntity entity);
	
	public void doNothing();

	Future<Integer> longProcessing();
}
