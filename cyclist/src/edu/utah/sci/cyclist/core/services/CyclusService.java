package edu.utah.sci.cyclist.core.services;

import java.util.UUID;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.core.model.CyclusJob;
import edu.utah.sci.cyclist.core.model.CyclusJob.Status;

public class CyclusService {
	public static final String CLOUDIUS_URL = "http://cycrun.fuelcycle.org";
	public static final String CLOUDIUS_SUBMIT_JOB = CLOUDIUS_URL + "/api/v1/job";
	public static final String CLOUDIUS_SUBMIT = CLOUDIUS_URL + "/api/v1/job-infile";
	public static final String CLOUDIUS_STATUS = CLOUDIUS_URL + "/api/v1/job-stat/";
	public static final String CLOUDIUS_LOAD   = CLOUDIUS_URL + "/api/v1/job-outfiles/";
	
	private ListProperty<CyclusJob> _jobs = new SimpleListProperty<>(FXCollections.observableArrayList());
	private Map<String, ScheduledService<JobStatus>> _running = new HashMap<>();
	static Logger log = Logger.getLogger(CyclusService.class);

	public CyclusService() {
		_jobs.addListener(new ListChangeListener<CyclusJob>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends CyclusJob> c) {
				while (c.next()) {
					for (CyclusJob job : c.getRemoved()) {
						ScheduledService<JobStatus> service = _running.remove(job.getId());
						if (service != null) {
							log.info("Job " + job.getAlias() + " canceled");
							service.cancel();
						}
					}
				}
			}
		});
	}

    public ListProperty<CyclusJob> jobs() {
        return _jobs;
    }

    public CyclusJob latestJob() {
        return _jobs.getValue().get(_jobs.getValue().size() - 1);
    }

    private void _submit(String path, Request request) {
        CyclusJob job = new CyclusJob(path);

        try {
            InputStream stream = request.execute().returnContent().asStream();
            JsonReader reader = Json.createReader(stream);
            JsonObject info = reader.readObject();
            job.setInfo(info);
            job.setStatus(Status.SUBMITTED);
            _jobs.add(job);
            poll(job);
        } catch (ClientProtocolException e) {
            log.error("Submit job communication error: "+ e.getMessage());
        } catch (IOException e) {
            log.error("submit job IO error: "+ e.getMessage());
        }
    }

    public void submit(File file) {
        String path = file.getAbsolutePath();
        Request request = Request.Post(CLOUDIUS_SUBMIT)
            .bodyFile(file, ContentType.DEFAULT_TEXT);
        this._submit(path, request);
    }

    public void submit(String file) {
        // file is a string of XML here that represents an input file,
        // rather than a file path
        String path = "cycic.xml";
        Request request = Request.Post(CLOUDIUS_SUBMIT)
            .bodyString(file, ContentType.DEFAULT_TEXT);
        this._submit(path, request);
    }

    public void submitCmd(String cmd, String... args) {
        String name = "cmd";
        String uid = UUID.randomUUID().toString().replace("-", "");
        String reqJSON = "{\"Id\": \"" + uid + "\", \"Cmd\": [\"" + cmd + "\"";
        for (String arg : args) {
            reqJSON += ", \"" + arg + "\"";
        }
        reqJSON += "]}";
        Request request = Request.Post(CLOUDIUS_SUBMIT_JOB)
            .bodyString(reqJSON, ContentType.APPLICATION_JSON);
        this._submit(name, request);
	}

    private void poll(final CyclusJob job) {
		final PollService service = new PollService(job);

		service.setDelay(Duration.seconds(0));
		service.setPeriod(Duration.seconds(5));

		service.lastValueProperty().addListener(
				new ChangeListener<JobStatus>() {

					@Override
					public void changed(
							ObservableValue<? extends JobStatus> observable,
							JobStatus prev, JobStatus current) {
						if (current.info != null) {
							current.job.setInfo(current.info);
							current.job.setStatus(current.status);
							switch (current.job.getStatus()) {
							case COMPLETED:
								service.cancel();
								_running.remove(job.getId());
								loadData(current.job);
								break;
							case FAILED:
								service.cancel();
								_running.remove(job.getId());
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

	private Path createSaveDirectory(String id) throws IOException {
		String home = System.getProperty("user.home");
		Path dir = Paths.get(home, "cyclist/data/", id);
		dir.toFile().mkdirs();
		return dir;
	}
	
	private void loadData(final CyclusJob job) {
		job.setStatus("loading");
		Task<JobStatus> task = new Task<JobStatus>() {
			protected JobStatus call() {
				String msg;
				try {
					Path dir = createSaveDirectory(job.getId());

					InputStream input = Request.Get(CyclusService.CLOUDIUS_LOAD + job.getId())
							.connectTimeout(1000).socketTimeout(100000)
							.execute().returnContent().asStream();
					saveZipStream(input, dir);
					job.setDatafilePath(dir.resolve("cyclus.sqlite").toString());
			
					msg = "ready";
				} catch (ClientProtocolException e) {
					log.error("Communication error while loading simulation data", e);
					msg = e.getMessage();
				} catch (IOException e) {
					log.error("IO error while loading simulation data" + e);
					msg = e.getMessage();
				}
				return new JobStatus(job, msg, null);
			}
		};

		task.valueProperty().addListener(new ChangeListener<JobStatus>() {
			@Override
			public void changed(
					ObservableValue<? extends JobStatus> observable,
					JobStatus prev, JobStatus newStatus) {
				job.setStatus(newStatus.status);
			}
		});

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}
	
	final int BUFFER = 2048;
	private void saveZipStream(InputStream input, Path dir) throws FileNotFoundException, IOException {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(input));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			int count;
            byte data[] = new byte[BUFFER];
            // write the files to the disk
            FileOutputStream fos = new FileOutputStream(dir.resolve(entry.getName()).toString());
            BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
            while ((count = zis.read(data, 0, BUFFER))  != -1) {
               dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
         }
         zis.close();
	}
}

class PollService extends ScheduledService<JobStatus> {
	private final CyclusJob _job;
	private static Logger log = Logger.getLogger(PollService.class);

	public PollService(CyclusJob job) {
		_job = job;
	}

	@Override
	protected Task<JobStatus> createTask() {
		return new Task<JobStatus>() {
			protected JobStatus call() {
				InputStream stream;
				try {
					stream = Request
							.Get(CyclusService.CLOUDIUS_STATUS + _job.getId())
							.connectTimeout(1000).socketTimeout(1000)
							.execute()
							.returnContent().asStream();

					if (isCancelled()) {
						return new JobStatus(_job, "cancelled", null);
					}
					
					JsonObject reply = Json.createReader(stream).readObject();
					return new JobStatus(_job, reply.getString("Status"), reply);
				} catch (ClientProtocolException e) {
					log.error("Communication error while polling remote execution service",	e);
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
