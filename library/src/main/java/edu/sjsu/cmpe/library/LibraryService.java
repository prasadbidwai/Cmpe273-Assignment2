package edu.sjsu.cmpe.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.stomp.StompConnection;
import edu.sjsu.cmpe.library.stomp.Listner;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;

public class LibraryService extends Service<LibraryServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Exception {
	new LibraryService().run(args);
    }

    @Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {
	bootstrap.setName("library-service");
	bootstrap.addBundle(new ViewBundle());
	bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(LibraryServiceConfiguration configuration,
	    Environment environment) throws Exception {
    	
	
	String queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicName();
	log.debug("Queue name is {}. Topic name is {}", queueName, topicName);
	String apolloUser = configuration.getApolloUser();
	String apolloPassword = configuration.getApolloPassword();
	String apolloHost = configuration.getApolloHost();
	int apolloPort = configuration.getApolloPort();
	String libraryName = configuration.getLibraryName();
	
	log.debug("{} - Queue name is {}. Topic name is {}",configuration.getLibraryName(), queueName,topicName);
	
    log.debug("{} - apollouser is {}. apollopassword is {}", libraryName, apolloUser,apolloPassword );
    
    log.debug("{} - apollo host is {}. apollo port is {}", configuration.getLibraryName(), apolloHost,apolloPort);
    
	environment.addResource(RootResource.class);
	
	BookRepositoryInterface bookRepository = new BookRepository();
	
	StompConnection apolloSTOMP = new  StompConnection(apolloUser, apolloPassword, apolloHost, apolloPort, libraryName, queueName, topicName, bookRepository);
	
	environment.addResource(new BookResource(bookRepository, apolloSTOMP));


	environment.addResource(new HomeResource(bookRepository));	
	
	
	environment.addServerLifecycleListener(new Listner(apolloSTOMP));
	
    }
}
