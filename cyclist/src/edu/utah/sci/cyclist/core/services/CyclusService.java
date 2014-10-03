package edu.utah.sci.cyclist.core.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.model.CyclusRun;

public class CyclusService {
	public static final String CLOUDIUS_URL = "http://cycrun.fuelcycle.org";
	public static final String CLOUDIUS_SUBMIT = CLOUDIUS_URL+"/api/v1/job-infile";
	public static final String CLOUDIUS_STATUS = CLOUDIUS_URL+"/api/v1/job/";
	
	private EventBus _eventBus;
	private ListProperty<CyclusRun> _runs = new SimpleListProperty<>(FXCollections.observableArrayList());
	private PollService _pollService = new PollService();
	
	
	public CyclusService(EventBus eventBus) {
		_eventBus = eventBus;
	}
	
	public ListProperty<CyclusRun> runs() {
		return _runs;
	}
	
	public void submit(File file) {
		CyclusRun run = new CyclusRun(file.getAbsolutePath());
		
		try {
			InputStream stream = Request.Post(CLOUDIUS_SUBMIT)
					.bodyFile(file, ContentType.DEFAULT_TEXT)
					.execute().returnContent().asStream();
			JsonReader reader = Json.createReader(stream);
			JsonObject info = reader.readObject();
			run.setInfo(info);
			synchronized(_runs) { _runs.add(run); }
			if (!_pollService.isAlive()) {
				_pollService = new PollService();
				_pollService.start();
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	class PollService extends Thread {
	
		private List<CyclusRun> _jobs;
		
		public void run() {
			for (;;) {
//				System.out.println("PollService: check runs");
				synchronized(_runs) {
					_jobs = _runs.filtered(r->{return r.getStatus() == CyclusRun.Status.SUMITTED; });
				}
//				System.out.println("PollSevice: "+_jobs.size()+" runs");
				for (CyclusRun run : _jobs) {
					try {
//						System.out.println("PollService: check run "+run.getId());
//						long t = System.currentTimeMillis();
						InputStream stream = Request.Get(CyclusService.CLOUDIUS_STATUS+run.getId())
								.connectTimeout(1000)
								.socketTimeout(1000)
								.execute().returnContent().asStream();
							
						JsonReader reader = Json.createReader(stream);
						JsonObject reply = reader.readObject();
//						System.out.println("PollService: status: "+reply.getString("Status"));
						run.updateInfo(reply);
//						long t1 = System.currentTimeMillis();
//						System.out.println("PollService: done in "+(t1-t)+" msec");
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (_jobs.size() == 0) {
//					System.out.println("PollService: done");
					return;
				}
				
				System.out.println("PollService: sleep");
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
