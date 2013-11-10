package uk.co.manifesto.wcs.publishevents.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;

public class PublishSession {

	private ICS ics;
	private String pubSessionId;
	private String pubSessionSql = "SELECT ps.id as session_id, pm.cs_text as message, ps.cs_sessiondate as session_startdate, ps.cs_enddate as session_enddate FROM PubSession ps, PubMessage pm WHERE ps.id=pm.cs_sessionid AND ps.id= ? AND pm.cs_type='RESOURCE'";
	
	public PublishSession(String pubSessionId, ICS ics) {
		this.ics = ics;
		this.pubSessionId = pubSessionId;

	}

	public List<AssetId> getPublishedAssets() {
		List<AssetId> publishedAssets = new ArrayList<AssetId>();
		
		PreparedStmt publishMessages = new PreparedStmt( pubSessionSql,Arrays.asList(new String[]{"PubMessage" , "PubSession"}));
		publishMessages.setElement(0, "PubSession", "id");
		StatementParam pubSessionIdParam = publishMessages.newParam();
		pubSessionIdParam.setString(0, pubSessionId);

		
		for (IList row : new IterableIListWrapper(ics.SQL(publishMessages, pubSessionIdParam, true))) {
		     try {
				String message = row.getValue("message");
				if (!message.startsWith("TABLE") && message.contains(":")) {
					AssetId candidate = AssetIdUtils.fromString(message);
					publishedAssets.add(candidate);
				}	
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}		
		return publishedAssets;
	}

}
