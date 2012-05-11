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

public class TestManagerEJBTest {
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
				TestEntity testEntity = new TestEntity();
				testEntity.setText("Thread:" + threadNumber + ", number:" + number);
				bean.create(testEntity);
//				bean.doNothing();
				counter++;
				number++;
			}
		}
	}
	
	 private static Connection connection;
	private static ContextSelector<EJBClientContext> selector;


	public static void main(String[] args) throws Exception {
//
//		final Hashtable jndiProperties = new Hashtable();
//		jndiProperties.put(Context.URL_PKG_PREFIXES,
//				"org.jboss.ejb.client.naming");
//		jndiProperties.put(Context.PROVIDER_URL, "remote://localhost:4447");
//		final Context context = new InitialContext(jndiProperties);

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
		Endpoint endpoint = Remoting.createEndpoint("endpoint", OptionMap.EMPTY);
	    endpoint.addConnectionProvider("remote", new RemoteConnectionProviderFactory(), OptionMap.create(Options.SSL_ENABLED, Boolean.FALSE));
	    
	    
//	    EJBClientContext.setSelector(selector);
	        // open a connection
	    IoFuture<Connection> futureConnection = endpoint.connect(new URI("remote://localhost:4447"), OptionMap.create(Options.SASL_POLICY_NOANONYMOUS, Boolean.FALSE), new AnonymousCallbackHandler());
	        connection = get(futureConnection, 5, TimeUnit.SECONDS);
		
	        
		StatelessEJBLocator<ITestManager> statelessEJBLocator = new StatelessEJBLocator<ITestManager>(ITestManager.class, "", "TestManagerEJB", TestManagerBean.class.getSimpleName(), "");
		
		EJBClientContext ejbClientContext = EJBClientContext.create();
		ejbClientContext.registerConnection(connection);
		ThreadLocal<EJBClientContext> threadLocal = new ThreadLocal<EJBClientContext>();
		threadLocal.set(ejbClientContext);
		selector = new ThreadLocalContextSelector<EJBClientContext>(threadLocal);
		
//		ejbClientContext.setSelector(selector);
		
		ITestManager bean = EJBClient.createProxy(statelessEJBLocator);
		System.out.println(bean.toString());
		if (bean == null) {
			throw new Exception("Proxy is null");
		}
//		TestEntity entity = new TestEntity();
//		bean.create(entity);
//		
//		final EJBClientContext ejbClientContext = EJBClientContext.create();
//        final ContextSelector<EJBClientContext> oldClientContextSelector = EJBClientContext.setConstantContext(ejbClientContext);
//        try {
//            ejbClientContext.registerConnection(connection);
//    		TestEntity entity2 = new TestEntity();
//    		bean.create(entity2);
//
//        } finally {
//            EJBClientContext.setSelector(oldClientContextSelector);
//        }
//        connection.close();
		
		ArrayList<CreateThread> threads = new ArrayList<TestManagerEJBTest.CreateThread>();
		for(int i=0; i<4;i++) {
//			ITestManager beanTestMgr = (ITestManager) context.lookup("ejb:" + appName
//					+ "/" + moduleName + "/" + distinctName + "/" + beanName + "!"
//					+ viewClassName);
			CreateThread thread = new CreateThread(bean, (i + 1));
			threads.add(thread);
		}
		long startTime = System.currentTimeMillis();
		for(CreateThread thread: threads) {
			thread.start();
		}
		
		Thread.sleep(10000);

		for (CreateThread createThread : threads) {
			createThread.stopp();
		}
		System.out.println("Time: " + (System.currentTimeMillis() - startTime));
		
		System.out.println("Counter: " + counter);
	}

}
