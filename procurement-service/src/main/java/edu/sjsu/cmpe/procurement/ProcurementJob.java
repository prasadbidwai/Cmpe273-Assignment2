package edu.sjsu.cmpe.procurement;

import javax.jms.Connection;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.domain.BookReq;
import edu.sjsu.cmpe.procurement.domain.BookShipped;
import edu.sjsu.cmpe.procurement.stomp.StompConnection;

@Every("300s")
public class ProcurementJob extends Job{
	@Override
	public void doJob() {
		
		 
		StompConnection apolloSTOMP = new StompConnection();
	    BookReq bookRequest;
	    Connection connection;
	    try {
	    	connection = apolloSTOMP.makeConnection();
			bookRequest = apolloSTOMP.reveiveQueueMessage(connection);
		    connection.close();
		    if (bookRequest.getOrder_book_isbns().size() != 0){
		    	System.out.println("Posting to Publisher");
		        Client client = Client.create();
		        String url = "http://54.193.56.218:9000/orders";	
		    	WebResource webResource = client.resource(url);
		    	ClientResponse response = webResource.accept("application/json")
		    			.type("application/json").entity(bookRequest, "application/json").post(ClientResponse.class);
		    	System.out.println(response.getEntity(String.class));
		        }
			} catch ( Exception e) {
				e.printStackTrace();
			}	
	    
	    
	   
	    try {
	    	String message;
	    	Client client = Client.create();
	    	String url = "http://54.193.56.218:9000/orders/00729";	
	    	WebResource webResource = client.resource(url);
	    	BookShipped response = webResource.accept("application/json")
	    			.type("application/json").get(BookShipped.class);
	    	connection = apolloSTOMP.makeConnection();
	    	System.out.println("response" + response);
	    	
	    	apolloSTOMP.publishTopicMessage(connection, response);
		     
			} catch ( Exception e) {
				e.printStackTrace();
			}
	    
	    }
}

