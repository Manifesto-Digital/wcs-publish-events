package uk.co.manifesto.wcs.publishevents;

import static com.fatwire.realtime.event.PublishingStatusEnum.DONE;
import static com.fatwire.realtime.util.RealTimeConstants.PublishingTasks.SESSION;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.co.manifesto.wcs.publishevents.session.PublishSession;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.event.EventException;
import com.fatwire.gst.foundation.facade.ics.ICSFactory;
import com.fatwire.realtime.event.PublishingEvent;
import com.fatwire.realtime.event.PublishingEventListener;

public class PublishListener implements PublishingEventListener {
    
	private static final Log log = LogFactory.getLog(PublishListener.class);
	 
	public void onEvent( PublishingEvent event ) throws EventException {
    	if (isPublishSessionComplete(event)) {
    		ICS ics = ICSFactory.getOrCreateICS();
    		PublishSession pubSession = new PublishSession(event.getPubSessionId(), ics);
    		log.info(String.format("Logging data for publish session %s", event.getPubSessionId()));
    		File file = getPublishSessionLogFile(event, ics);
    		log.info(String.format("Creating log file  %s", file.getAbsolutePath()));
 		
    		try {
				writePublishedAssetsToFile(pubSession, file);
			} catch (IOException e) {
				log.error(e.getLocalizedMessage());
			}
    	}
    }

	private File getPublishSessionLogFile(PublishingEvent event, ICS ics) {
		String exportLocation = ics.GetProperty("cs.csdtfolder") + "/publish";
		makeSureFolderExists(exportLocation);
		File file = new File(String.format("%s/pub-session-%s.txt",exportLocation, event.getPubSessionId()));
		return file;
	}

	private void makeSureFolderExists(String exportLocation) {
		File dir = new File(exportLocation);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	private void writePublishedAssetsToFile(PublishSession pubSession, File file) throws IOException {
		PrintWriter publishedAssetsWriter = new PrintWriter(new FileWriter(file));
		for (AssetId assetId : pubSession.getPublishedAssets()) {
		    publishedAssetsWriter.println(String.format("Published asset: %s", assetId.toString()));
		}
		publishedAssetsWriter.close();
	}


	private boolean isPublishSessionComplete(PublishingEvent event) {
		return event.getTaskName().equals(SESSION) && event.getStatus().equals(DONE);
	}
}