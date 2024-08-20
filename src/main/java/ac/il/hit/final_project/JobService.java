package ac.il.hit.final_project;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
 * part of the buisness logic
 * @Service- marks that the one handling the service and the injection of the dependency would be spring.
 * connectes between the repository and the controller aka the layers connection to the db and the layer handling user interaction.
 */
@Service
public class JobService {
    private final JobRepository jobRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**`
     * constructor-based Dependency Injection (DI)
     * @param jobRepository the jobRepository to acsess db functions
     */
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    //a function to validate status range
    private Boolean validateJob(Job job) {
        if (job.getStatus() < 100 || job.getStatus() > 999) {
            throw new RuntimeException("Illegal status");
        }
        return true;
    }

    // Create functions

    /**
     * Saves a new job
     * @param job Job entity to save
     * @return saved job if successful
     */
    public Job saveJob(Job job) {
        validateJob(job);
        return jobRepository.save(job);
    }

    // Read functions

    /**
     * Retrieves all jobs
     * @return list of jobs
     */
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Optional<Job> findJobById(Long id) {
        return jobRepository.findById(id);
    }

    public List<Job> findJobByStatus(Integer status) {
        return jobRepository.findByStatus(status);
    }

    public List<Job> findJobByType(String type) {
        return jobRepository.findByJobType(type);
    }

    public List<Job> findJobByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return jobRepository.findByCreatedAtBetween(startDate, endDate);
    }

    // Update functions

    /**
     * Update a possibly existing job
     * @param id ID of the job to update
     * @param jobDetails all details from JSON in the request body
     * @return updated job entity
     * @throws RuntimeException if id is not in database
     */
    public Job updateJob(Long id, Job jobDetails) {
        Job job = jobRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Job not found"));
        job.setJobName(jobDetails.getJobName());
        if(validateJob(jobDetails))
            {job.setStatus(jobDetails.getStatus());}
        job.setJobType(jobDetails.getJobType());
        //job.setUpdatedAt(LocalDateTime.now()); -> the db as onUpdate() fun that automaticliy activated on change!
        return jobRepository.save(job);
    }

    public Job updateJobPassword(Long id, String oldPassword, String newPassword) {
        Job job = jobRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Job not found"));
        if (!passwordEncoder.matches(oldPassword, job.getPassword())) {
            throw new RuntimeException("Wrong password");
        } else {
            job.setPassword(newPassword);
            return jobRepository.save(job);
        }
    }

    public Job updateJobStatus(Long id, Integer status){
        Job job = jobRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Job not found"));
        if(validateJob(job)) {
            job.setStatus(status);
        }
        return jobRepository.save(job);
    }

    // Delete functions

    public void deleteJob(Long id) {
        jobRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Job not found"));
        jobRepository.deleteById(id);
    }
}