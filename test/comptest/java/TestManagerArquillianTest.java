import java.io.File;
import java.util.Date;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.acme.testmanager.ITestManager;
import de.acme.testmanager.TestManagerBean;
import de.acme.testmanager.entities.TestEntity;

@RunWith(Arquillian.class)
public class TestManagerArquillianTest {

	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "testmanager.jar")
				.addClasses(ITestManager.class, TestManagerBean.class,
						TestEntity.class)
				.addAsManifestResource(
						new File("src/conf/META-INF/persistence.xml"),
						"persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@EJB
	private ITestManager testManager;

	@Test
	public void createTestEntityCall() throws Exception {
		Assert.assertNotNull(testManager);
		TestEntity testEntity = new TestEntity();
		testEntity.setText("BlaBla");
		testManager.create(testEntity);
	}
	
	@Test
	public void callAsynchronousMethod() throws Exception {
		Assert.assertNotNull(testManager);
		Future future = testManager.longProcessing();
		while (!future.isDone()){
             Thread.sleep(1000);
             System.out.println(new Date().toString()+" - I do other things ...");
        }
		
		Integer ret = (Integer)future.get();
        System.out.println(new Date().toString()+" - ret : "+ret);

	}
}
