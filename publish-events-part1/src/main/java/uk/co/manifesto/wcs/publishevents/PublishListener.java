package uk.co.manifesto.wcs.publishevents;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.cs.core.event.EventException;
import com.fatwire.realtime.event.PublishingEvent;
import com.fatwire.realtime.event.PublishingEventListener;

public class PublishListener implements PublishingEventListener {
    
	private static final Log log = LogFactory.getLog(PublishListener.class);
	
	public void onEvent( PublishingEvent event ) throws EventException {
		log.info(String.format("Publish task: %s",event.getTaskName()));
		log.info(String.format("Publish status: %s",event.getStatus()));
		log.info(String.format("Publish message: %s",event.getMessage()));
		
    }
}