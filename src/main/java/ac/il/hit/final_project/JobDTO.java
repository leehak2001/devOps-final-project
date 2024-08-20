package ac.il.hit.final_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

@Value
@JsonPropertyOrder({"jobId", "jobName", "status", "jobType"})
public class JobDTO {
    @JsonIgnore
    private final Job job;

    public JobDTO(Job job) {
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null");
        }
        this.job = job;
    }

    public Long getJobId() { return job.getId(); }
    public String getJobName() { return job.getJobName(); }
    public Integer getStatus() { return job.getStatus(); }
    public String getJobType() { return job.getJobType(); }
}

