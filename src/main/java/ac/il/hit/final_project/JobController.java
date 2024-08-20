package ac.il.hit.final_project;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * `@ -GetMapping("/jobs")`: Retrieve all jobs.
 * `@ -PostMapping("/jobs")`: Create a new job.
 * `@ -GetMapping("/jobs/{id}")`: Retrieve a job by ID.
 * `@ -PutMapping("/jobs/{id}")`: Update a job.
 * `@ -DeleteMapping("/jobs/{id}")`: Delete a job.
 * `@ -GetMapping("/jobs/status/{status}")`: Retrieve jobs by status.
 * `@ -GetMapping("/jobs/jobType/{jobType}")`: Retrieve jobs by job type.
 * `@ -GetMapping("/jobs/date-range")`: Retrieve jobs by a date range.
 * aditional:
 *  @PutMapping("/{id}/password"): update job password
 *  @PutMapping("/{id}/status"): update job status
 */

/**
 * REST Controller for Job entity
 * This controller handles HTTP requests and responses for job interaction
 * and operations
 */
@RestController // the methods will handle HTTP requests and return data directly as the response body, typically in JSON format.
@RequestMapping("/jobs")// Defines the base URL path for the controller.
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    //`@ -GetMapping("/jobs")`: Retrieve all jobs.
    /**
     * Gets all the jobs in the Job table using the JobService
     * @return an HTTP Response including a JSON with an Array of JSONs,
     * representing all the jobs in our database.
     * Successful requests results in status code 200
     */
    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs(){
        List<Job> jobs = jobService.getAllJobs();
        List<JobDTO> jobDTOs = jobs.stream().map(JobDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(jobDTOs);
    }

    //@ -PostMapping("/jobs")`: Create a new job.
    @PostMapping
    public ResponseEntity<JobDTO> createJob(@RequestBody Job job){
        try {
            Job savedJob = jobService.saveJob(job);
            //return status 201 CREATED
            return ResponseEntity.status(HttpStatus.CREATED).body(new JobDTO(savedJob));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    //`@ -GetMapping("/jobs/{id}")`: Retrieve a job by ID.
    //if found return atatus 200 else return status 404-not found
    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable long id){
        return jobService.findJobById(id)
                .map(job -> ResponseEntity.ok(new JobDTO(job)))
                .orElse(ResponseEntity.notFound().build());
    }

    //`@ -PutMapping("/jobs/{id}")`: Update a job.
    //returning status 204-no content without returning the updated job
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateJob(@PathVariable Long id, @RequestBody Job job){
        try {
            jobService.updateJob(id, job);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
    //PUT: Used to fully update or create a specific resource

    //`@ -DeleteMapping("/jobs/{id}")`: Delete a job.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable long id){
        // returns an HTTP Response with empty body and status code 204.
        // adds no-content header to the response
        //  return ResponseEntity.noContent().build();
        try {
            jobService.deleteJob(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    //`@ -GetMapping("/jobs/status/{status}")`: Retrieve jobs by status.
    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobDTO>> getJobByStatus(@PathVariable Integer status){
        List<Job> jobs = jobService.findJobByStatus(status);
        List<JobDTO> jobDTOs = jobs.stream().map(JobDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(jobDTOs);
    }

    // * `@ -GetMapping("/jobs/jobType/{jobType}")`: Retrieve jobs by job type.
    @GetMapping("/jobType/{jobType}")
    public ResponseEntity<List<JobDTO>> getJobByType(@PathVariable String jobType){
        List<Job> jobs = jobService.findJobByType(jobType);
        List<JobDTO> jobDTOs = jobs.stream().map(JobDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(jobDTOs);
    }


    //`@ -GetMapping("/jobs/date-range")`: Retrieve jobs by a date range.
    @PostMapping("/date-range")
    public ResponseEntity<List<JobDTO>> getByCreatedBetween(@RequestBody DateRangeRequest dateRangeRequest) {
        LocalDateTime startDate = dateRangeRequest.getStartDate();
        LocalDateTime endDate = dateRangeRequest.getEndDate();

        List<Job> jobs = jobService.findJobByDateRange(startDate, endDate);
        List<JobDTO> jobDTOs = jobs.stream().map(JobDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(jobDTOs);
    }



    //allows the change of an old password to a new one
    @PutMapping("/{id}/password")
    public ResponseEntity<JobDTO> updateJobPassword(@PathVariable Long id, @RequestBody UpdatePasswordRequest passwordRequest){
        try {
            Job job = jobService.updateJobPassword(id, passwordRequest.getOldPassword(), passwordRequest.getNewPassword());
            return ResponseEntity.ok(new JobDTO(job));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    //returns an empty body and status code 204 if uupdated status coretcliy else 400.
    @PutMapping("/status/{id}")
    public ResponseEntity<Void> updateJobStatus(@PathVariable Long id, @RequestBody Integer status)
    {
        try {
            jobService.updateJobStatus(id, status);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

}

