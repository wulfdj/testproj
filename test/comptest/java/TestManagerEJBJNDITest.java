import java.net.URI;
import java.security.Security;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClient;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.StatelessEJBLocator;
import org.jboss.ejb.client.ThreadLocalContextSelector;
import org.jboss.remoting3.Connection;
import org.jboss.remoting3.Endpoint;
import org.jboss.remoting3.Remoting;
import org.jboss.remoting3.remote.RemoteConnectionProviderFactory;
import org.jboss.sasl.JBossSaslProvider;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.Options;

import de.acme.testmanager.ITestManager;
import de.acme.testmanager.TestManagerBean;
import de.acme.testmanager.entities.TestEntity;

import static org.jboss.ejb.client.remoting.IoFutureHelper.get;

public class TestManagerEJBJNDITest {
	// STATIC BLOCK - YOU MUST HAVE THIS
	
	static long counter = 0;

	static {
		Security.addProvider(new JBossSaslProvider());
	}

	public static class CreateThread extends Thread {

		ITestManager bean = null;
		boolean running = true;
		long number = 1;
		int threadNumber = 0;
		
		public CreateThread(ITestManager bean, int threadNumber) {
			this.bean = bean;
			this.threadNumber =threadNumber;  
		}
		
		public void stopp() {
			running = false;
		}

		public void run() {
			while (running) {
//			for(;;) {
				TestEntity testEntity = new TestEntity();
				testEntity.setText("Thread:" + threadNumber + ", number:" + number);
				bean.create(testEntity);
//				bean.doNothing();
//				counter++;
				number++;
//			}
			}
		}
	}
	
	 private static Connection connection;
	private static ContextSelector<EJBClientContext> selector;


	public static void main(String[] args) throws Exception {

		final Hashtable jndiProperties = new Hashtable();
		jndiProperties.put(Context.URL_PKG_PREFIXES,
				"org.jboss.ejb.client.naming");
		jndiProperties.put(Context.PROVIDER_URL, "remote://localhost:4447");
		final Context context = new InitialContext(jndiProperties);

		final String appName = "";

		final String moduleName = "TestManagerEJB";
		// THIS IS THE NAME OF THE JAR WITH YOUR EJBs. Write its name here,
		// without the .jar.

		final String distinctName = "";
		// &nbsp;&nbsp; &nbsp;AS7 allows deployments to have an distinct name.
		// If you don't use this feature, let this field empty.

		final String beanName = TestManagerBean.class.getSimpleName();
		// EJB CLASS WITH THE IMPLEMENTATION (simple name)

		final String viewClassName = ITestManager.class.getName();
		// FULLY QUALIFIED NAME OF THE REMOTE CLASS (interface).

		
//		ArrayList<Long> callingTime = new ArrayList<Long>();
//		long startTime = System.currentTimeMillis();
//		for (int i = 0; i < 1000; i++) {
//			TestEntity testEntity = new TestEntity();
//			testEntity.setText("Text" + i);
//			long callStart = System.currentTimeMillis();
//			bean.create(testEntity);
//			callingTime.add(System.currentTimeMillis() - callStart);
//			// System.out.println(testEntity.getId());
//		}
//		long endTime = System.currentTimeMillis();
//		System.out.println("Took: " + (endTime - startTime));
//		System.out.println("-------------------------------");
//		for (Long entry : callingTime) {
//			System.out.println(":" + entry + " ms");
//
//		}
		
		ITestManager bean = (ITestManager) context.lookup("ejb:" + appName
				+ "/" + moduleName + "/" + distinctName + "/" + beanName + "!"
				+ viewClassName);
		
		long startTime = System.currentTimeMillis();
//		for (int i = 0; i < 1000; i++) {
//			bean.doNothing();
//		}
//		long endTime = System.currentTimeMillis();
//		System.out.println("doNothing() Took: " + (endTime - startTime));
//
//		startTime = System.currentTimeMillis();
//		for (int i = 0; i < 1000; i++) {
//			TestEntity testEntity = new TestEntity();
//			testEntity.setText("Thread:1, number:" + i);
//			bean.create(testEntity);
//		}
//		endTime = System.currentTimeMillis();
//		System.out.println("Took: " + (endTime - startTime));
//		System.exit(0);
		
		
		
		
		
		
		ArrayList<CreateThread> threads = new ArrayList<TestManagerEJBJNDITest.CreateThread>();
		for(int i=0; i<1;i++) {
			CreateThread thread = new CreateThread(bean, (i + 1));
			threads.add(thread);
		}
		startTime = System.currentTimeMillis();
		for(CreateThread thread: threads) {
			thread.start();
		}
		
		Thread.sleep(10000);

		for (CreateThread createThread : threads) {
			createThread.stopp();
		}
		System.out.println("Time: " + (System.currentTimeMillis() - startTime));
		
		System.out.println("Counter: " + counter);
		System.exit(0);
	}

}
