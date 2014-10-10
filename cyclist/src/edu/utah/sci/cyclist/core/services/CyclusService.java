package edu.utah.sci.cyclist.core.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.core.model.CyclusJob;
import edu.utah.sci.cyclist.core.model.CyclusJob.Status;

public class CyclusService {
	public static final String CLOUDIUS_URL = "http://cycrun.fuelcycle.org";
	public static final String CLOUDIUS_SUBMIT = CLOUDIUS_URL+"/api/v1/job-infile";
	public static final String CLOUDIUS_STATUS = CLOUDIUS_URL+"/api/v1/job-stat/";
	public static final String CLOUDIUS_LOAD = CLOUDIUS_URL+"/api/v1/job-outfiles/";

	private ListProperty<CyclusJob> _jobs = new SimpleListProperty<>(FXCollections.observableArrayList());
	private Map<String, ScheduledService<JobStatus>> _running = new HashMap<>();
	static Logger log = Logger.getLogger(CyclusService.class);
			
	public CyclusService() {
		_jobs.addListener(new ListChangeListener<CyclusJob>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends CyclusJob> c) {
				while (c.next()) {
					if (c.wasPermutated()) {

					} else if (c.wasUpdated()) {

					} else {
						for (CyclusJob job : c.getRemoved()) {
							ScheduledService<JobStatus> service = _running.remove(job.getId());
							if (service != null) {
								log.info("Job "+job.getAlias()+" canceled");
								service.cancel();
							}
						}
					}
				}

			}
		});
	}

	public ListProperty<CyclusJob> jobs() {
		return _jobs;
	}

	public void submit(File file) {
		CyclusJob job = new CyclusJob(file.getAbsolutePath());

		try {
			InputStream stream = Request.Post(CLOUDIUS_SUBMIT)
					.bodyFile(file, ContentType.DEFAULT_TEXT)
					.execute().returnContent().asStream();
			JsonReader reader = Json.createReader(stream);
			JsonObject info = reader.readObject();
			job.setInfo(info);
			job.setStatus(Status.SUBMITTED);
			_jobs.add(job);
			poll(job);

		} catch (ClientProtocolException e) {
			log.error("Submit job communication error", e);
		} catch (IOException e) {
			log.error("Submit job IO error", e);
		}
	}

	private void poll(final CyclusJob job) {
		final PollService service = new PollService(job);

		service.setDelay(Duration.seconds(0));
		service.setPeriod(Duration.seconds(5));

		service.lastValueProperty().addListener(new ChangeListener<JobStatus>() {

			@Override
			public void changed(ObservableValue<? extends JobStatus> observable, JobStatus prev, JobStatus current) {
				if (current.info != null) {
					current.job.setInfo(current.info);
					current.job.setStatus(current.status);
					switch (current.job.getStatus()) {
					case COMPLETED:
						service.cancel();
						_running.remove(service.getJob().getId());
						loadData(current.job);
						break;
					case FAILED:
						service.cancel();
						_running.remove(service.getJob().getId());
						break;
					case SUBMITTED:
					case INIT:
						break;
					case LOADING:
					case READY:
						// cannot occur during this stage
						break;
					}	
				}
			}
		});
		
		_running.put(job.getId(), service);
		service.start();
	}

	private void loadData(final CyclusJob job) {
		job.setStatus(Status.LOADING);
		Task<JobStatus> task = new Task<JobStatus> () {
			protected JobStatus call() {
				try {
					Path dir = Paths.get("/Users/yarden/data/neup/",job.getId());
					Path file = dir.resolve("data.zip");
					
					Files.createDirectory(dir);
					
					Request.Get(CyclusService.CLOUDIUS_LOAD+job.getId())
							.connectTimeout(1000)
							.socketTimeout(1000)
							.execute().saveContent(file.toFile());
					
					Process process = Runtime.getRuntime().exec("unzip data.zip", null, dir.toFile());
					InputStream is = process.getInputStream();
				    InputStreamReader isr = new InputStreamReader(is);
				    BufferedReader br = new BufferedReader(isr);
				    String line;
				    
				    while ((line = br.readLine()) != null) {
				    	log.info("unzip: "+line);
				    }
				    job.setDatafilePath(dir.resolve("cyclus.sqlite").toString());
;					return new JobStatus(job, "ready", null);
				} catch (ClientProtocolException e) {
					log.error("Communication error while loading simulation data",e);
					return new JobStatus(job, e.getMessage(), null);
				} catch (IOException e) {
					log.error("IO error while loading simulation data"+e);
					return new JobStatus(job, e.getMessage(), null);
				}
			}
		};
		
		task.valueProperty().addListener(new ChangeListener<JobStatus>() {

			@Override
            public void changed(
                    ObservableValue<? extends JobStatus> observable,
                    JobStatus prev, JobStatus status) {
	            job.setStatus(status.status);          
            }
		});
		
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}
}


class PollService extends ScheduledService<JobStatus> {
	private final CyclusJob _job;
	private static Logger log =  Logger.getLogger(PollService.class);
	
	public PollService(CyclusJob job) {
		_job = job;
	}
	
	public CyclusJob getJob() { return _job; }
	
	@Override
	protected Task<JobStatus> createTask() {
		return new Task<JobStatus>() {
			protected JobStatus call() {
				InputStream stream;
				try {
					stream = Request.Get(CyclusService.CLOUDIUS_STATUS+_job.getId())
							.connectTimeout(1000)
							.socketTimeout(1000)
							.execute().returnContent().asStream();

					if (isCancelled()) {
						return new JobStatus(_job, "cancelled", null);
					}
					JsonReader reader = Json.createReader(stream);
					JsonObject reply = reader.readObject();
					return new JobStatus(_job, reply.getString("Status"), reply);
				} catch (ClientProtocolException e) {
					log.error("Communication error while polling remote execution service", e);
					return new JobStatus(_job, e.getMessage(), null);
				} catch (IOException e) {
					log.error("IO error while polling remote execution service", e);
					return new JobStatus(_job, e.getMessage(), null);
				}
			}
		};
	}
}

class JobStatus {
	CyclusJob job;
	String status;
	JsonObject info;

	public JobStatus(CyclusJob job, String status, JsonObject info) {
		this.job = job;
		this.status = status;
		this.info = info;
	}
}




