package edu.utah.sci.cyclist.core.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.model.CyclusJob;

public class CyclusService {
	public static final String CLOUDIUS_URL = "http://cycrun.fuelcycle.org";
	public static final String CLOUDIUS_SUBMIT = CLOUDIUS_URL+"/api/v1/job-infile";
	public static final String CLOUDIUS_STATUS = CLOUDIUS_URL+"/api/v1/job/";
	public static final String CLOUDIUS_DATA = CLOUDIUS_URL+"/api/v1/job-outfiles/";
	
	private ListProperty<CyclusJob> _runs = new SimpleListProperty<>(FXCollections.observableArrayList());
	private Boolean _isPolling = false;
	private PollService _pollService = new PollService();
	private List<CyclusJob> _jobs;
	
	
	public CyclusService() {
	}
	
	public ListProperty<CyclusJob> jobs() {
		return _runs;
	}
	
	public void submit(File file) {
		CyclusJob run = new CyclusJob(file.getAbsolutePath());
		
		try {
			InputStream stream = Request.Post(CLOUDIUS_SUBMIT)
					.bodyFile(file, ContentType.DEFAULT_TEXT)
					.execute().returnContent().asStream();
			JsonReader reader = Json.createReader(stream);
			JsonObject info = reader.readObject();
			run.setInfo(info);
			_runs.add(run);
			poll();
			
//			if (!_pollService.isAlive()) {
//				_pollService = new PollService();
//				_pollService.start();
//			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void poll() {
		if (_isPolling) return;
		

		final List<CyclusJob> jobs = _runs.filtered(r->{return r.getStatus() == CyclusJob.Status.SUBMITTED; });
		if (_jobs.size() == 0) {
			_isPolling = false;
		} else {
			_isPolling = true;
			Task<ObservableList<Entry>> task = new Task<ObservableList<Entry>>() {
				@Override
				protected ObservableList<Entry> call() throws Exception {
					List<Entry> list = new ArrayList<>();
					for (CyclusJob job : jobs) {
						try {
//							System.out.println("PollService: check run "+run.getId());
//							long t = System.currentTimeMillis();
							InputStream stream = Request.Get(CyclusService.CLOUDIUS_STATUS+job.getId())
									.connectTimeout(1000)
									.socketTimeout(1000)
									.execute().returnContent().asStream();
								
							JsonReader reader = Json.createReader(stream);
							JsonObject reply = reader.readObject();
//							System.out.println("PollService: status: "+reply.getString("Status"));
							list.add(new Entry(job, reply));
//							long t1 = System.currentTimeMillis();
//							System.out.println("PollService: done in "+(t1-t)+" msec");
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					return FXCollections.observableList(list);
				}			
			};
			
			task.valueProperty().addListener(new ChangeListener<ObservableList<Entry>>() {

				@Override
				public void changed(
						ObservableValue<? extends ObservableList<Entry>> observable,
						ObservableList<Entry> oldValue,
						ObservableList<Entry> newValue) {
					for (Entry entry : newValue) {
						entry.job.updateInfo(entry.reply);
					}
					
					
				}
			});
			
			Thread th = new Thread(task);
			th.setDaemon(true);
			th.start();
		}
	}
	
	class Entry {
		CyclusJob job;
		JsonObject reply;
		
		public Entry(CyclusJob job, JsonObject reply) {
			this.job = job;
			this.reply = reply;
		}
	}
	
	class PollService extends Thread {
	
		private List<CyclusJob> _jobs;
		
		public void run() {
			for (;;) {
//				System.out.println("PollService: check runs");
				synchronized(_runs) {
					_jobs = _runs.filtered(r->{return r.getStatus() == CyclusJob.Status.SUBMITTED; });
				}
//				System.out.println("PollSevice: "+_jobs.size()+" runs");
				for (CyclusJob run : _jobs) {
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
