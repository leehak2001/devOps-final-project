package ac.il.hit.final_project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the JobController.
 * This class tests the interaction between JobController, JobService, and JobRepository.
 */

//@ExtendWith(SpringExtension.class): Integrates Spring TestContext Framework with JUnit 5.
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CICDJobControllerTest {

    //@Autowired: Injects dependencies like JobController, JobService, and JobRepository
    @Autowired
    private JobController jobController;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobRepository jobRepository;

    @BeforeEach
    void setUp() {
        // Clean the database before each test to ensure a consistent state.
        jobRepository.deleteAll();
    }

    /*
     * testCreateJob: Verifies that creating a job via the controller returns a 201 CREATED status
     * and the job's details are correctly returned in the response.
     */
    @Test
    void testCreateJob() {
        // Given a new Job object.
        Job job = new Job("Job1", "Type1", 200);

        // When the job is created via the controller.
        ResponseEntity<JobDTO> response = jobController.createJob(job);

        // Then the response status should be 201 CREATED.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // And the response body should not be null.
        assertNotNull(response.getBody());

        // And the job's name should be "Job1".
        assertEquals("Job1", response.getBody().getJobName());
        assertEquals("Type1", response.getBody().getJobType());

        //checking that the job can be found after creation
        Optional<Job> foundJob = jobService.findJobById(response.getBody().getJobId());
        assertTrue(foundJob.isPresent());

        //checking if what is saved in the db is the same as what was requsted
        assertEquals(foundJob.get().getJobName(), response.getBody().getJobName());
        assertEquals(foundJob.get().getJobType(), response.getBody().getJobType());
    }

    /* testGetAllJobs: Ensures that retrieving all jobs returns a 200 OK status and the correct number
     * of jobs.
     */
    @Test
    void testGetAllJobs() {
        // Given two Job objects.
        Job job1 = new Job("Job1", "Type1", 200);
        Job job2 = new Job("Job2", "Type2", 300);

        // When both jobs are saved via the service.
        jobService.saveJob(job1);
        jobService.saveJob(job2);

        // And retrieved via the controller.
        ResponseEntity<List<JobDTO>> response = jobController.getAllJobs();

        // Then the response status should be 200 OK.
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // And the response body should contain both jobs.
        assertThat(response.getBody()).hasSize(2);
    }

    /*
     * testGetJobById: Checks that retrieving a job by its ID returns a 200 OK status and
     *  the correct job details.
     */
    @Test
    void testGetJobById() {
        // Given a new Job object.
        Job job = new Job("Job1", "Type1", 200);

        // When the job is saved via the service.
        Job savedJob = jobService.saveJob(job);

        // And retrieved by its ID via the controller.
        ResponseEntity<JobDTO> response = jobController.getJobById(savedJob.getId());

        // Then the response status should be 200 OK.
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // And the response body should not be null.
        assertNotNull(response.getBody());

        // And the job's ID should match the saved job's ID.
        assertEquals(savedJob.getId(), response.getBody().getJobId());
    }

    /*
     *testUpdateJob: Validates that updating a job returns a 204 NO CONTENT status and
     * the job's details are correctly updated in the database.
     */
    @Test
    void testUpdateJob() {
        // Given a new Job object.
        Job job = new Job("Job1", "Type1", 200);

        // When the job is saved via the service.
        Job savedJob = jobService.saveJob(job);

        // And updated via the controller.
        Job updatedJob = new Job("UpdatedJob", "UpdatedType", 300);
        ResponseEntity<Void> response = jobController.updateJob(savedJob.getId(), updatedJob);

        // Then the response status should be 204 NO CONTENT.
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // And the job's details should be updated in the database.
        Job fetchedJob = jobService.findJobById(savedJob.getId()).get();
        assertEquals("UpdatedJob", fetchedJob.getJobName());
        assertEquals("UpdatedType", fetchedJob.getJobType());
        assertEquals(300, fetchedJob.getStatus());
    }

    /*
     *testUpdateStatus: Validates that updating a job status returns a 204 NO CONTENT status and
     * the job's status is correctly updated in the database.
     */
    @Test
    void testUpdateStatus() {
        // Given a new Job object.
        Job job = new Job("Job1", "Type1", 200);

        // When the job is saved via the service.
        Job savedJob = jobService.saveJob(job);

        // And updated via the controller.
        int newStatus = 300;
        ResponseEntity<Void> response = jobController.updateJobStatus(savedJob.getId(), newStatus);

        // Then the response status should be 204 NO CONTENT.
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // And the job's details should be updated in the database.
        Job fetchedJob = jobService.findJobById(savedJob.getId()).get();
        assertEquals(newStatus, fetchedJob.getStatus());
    }


    /*
     *testDeleteJob: Confirms that deleting a job returns a 204 NO CONTENT status and
     *  the job is removed from the database.
     */
    @Test
    void testDeleteJob() {
        // Given a new Job object.
        Job job = new Job("Job1", "Type1", 200);

        // When the job is saved via the service.
        Job savedJob = jobService.saveJob(job);

        // And deleted via the controller.
        ResponseEntity<Void> response = jobController.deleteJob(savedJob.getId());

        // Then the response status should be 204 NO CONTENT.
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // And the job should no longer be present in the database.
        assertFalse(jobService.findJobById(savedJob.getId()).isPresent());
    }

    /*
     * testGetJobByStatus: Verifies that retrieving jobs by their status returns a 200 OK
     *  status and the correct list of jobs.
     */
    @Test
    void testGetJobByStatus() {
        // Given two Job objects with the same status.
        Job job1 = new Job("Job1", "Type1", 200);
        Job job2 = new Job("Job2", "Type2", 200);
        Job job3 = new Job("Job3", "Type3", 300);

        // When both jobs are saved via the service.
        jobService.saveJob(job1);
        jobService.saveJob(job2);
        jobService.saveJob(job3);

        // And retrieved by status via the controller.
        ResponseEntity<List<JobDTO>> response = jobController.getJobByStatus(200);

        // Then the response status should be 200 OK.
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // And the response body should contain both jobs.
        assertThat(response.getBody()).hasSize(2);
    }

    /*
     * testGetJobByType: Ensures that retrieving jobs by their job type returns a 200 OK
     *  status and the correct list of jobs.
     */
    @Test
    void testGetJobByType() {
        // Given two Job objects with the same job type.
        Job job1 = new Job("Job1", "Type1", 200);
        Job job2 = new Job("Job2", "Type1", 300);
        Job job3 = new Job("Job3", "Type2", 300);

        // When both jobs are saved via the service.
        jobService.saveJob(job1);
        jobService.saveJob(job2);
        jobService.saveJob(job3);

        // And retrieved by job type via the controller.
        ResponseEntity<List<JobDTO>> response = jobController.getJobByType("Type1");

        // Then the response status should be 200 OK.
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // And the response body should contain both jobs.
        assertThat(response.getBody()).hasSize(2);
    }

    /*
     * testUpdateJobWithInvalidData: Ensures that updating a job with invalid data
     * returns a bad request status.
     */
    @Test
    void testUpdateJobWithInvalidData() {
        // Given a new Job object.
        Job job = new Job("Job1", "Type1", 200);

        // When the job is saved via the service.
        Job savedJob = jobService.saveJob(job);

        // And updated via the controller with invalid data.
        Job updatedJob = new Job("UpdatedJob", "UpdatedType", 9999);

        // Then expect a RuntimeException when trying to update with invalid status.
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            jobController.updateJob(savedJob.getId(), updatedJob);
        });

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("Illegal status", thrown.getReason());

        //checking that the job can was not updated
        Optional<Job> foundJob = jobService.findJobById(savedJob.getId());
        assertTrue(foundJob.isPresent());

        //checking if what is saved in the db is the same as what was requsted
        assertEquals(foundJob.get().getStatus(),savedJob.getStatus());
    }



    /*
     * testDeleteJobNonexistent: Ensures that deleting a nonexistent job
     * returns a not found status.
     */
    @Test
    void testDeleteJobNonexistent() {
        Long nonexistentJobId = 999L;
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            jobController.deleteJob(nonexistentJobId);
        });
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("Job not found", thrown.getReason());
    }

    /*
     * testGetByCreatedBetween: Ensures that retrieving jobs by a date range
     * returns the correct list of jobs.
     */
    @Test
    void testGetByCreatedBetween() {
        // Given jobs with current date times.
        Job job1 = new Job("Job1", "Type1", 200);
        Job job2 = new Job("Job2", "Type2", 300);
        Job job3 = new Job("Job3", "Type3", 300);

        // Set the creation dates using a service or repository that sets it.
        jobService.saveJob(job1);
        jobService.saveJob(job2);
        jobService.saveJob(job3);

        // When retrieved by date range via the controller.
        ResponseEntity<List<JobDTO>> response = jobController.getByCreatedBetween(
                new DateRangeRequest(LocalDateTime.now().minusDays(4), LocalDateTime.now()));

        // Then the response status should be 200 OK.
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // And the response body should contain all three jobs.
        assertThat(response.getBody()).hasSize(3);
    }

    @Test
    void testUpdateJobPassword() {
        Job job = new Job("Job1", "Type1", 200, "oldPassword");
        Job savedJob = jobService.saveJob(job);
        UpdatePasswordRequest passwordRequest = new UpdatePasswordRequest("oldPassword", "newPassword");

        ResponseEntity<JobDTO> response = jobController.updateJobPassword(savedJob.getId(), passwordRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Job updatedJob = jobService.findJobById(savedJob.getId()).get();
        assertNotNull(updatedJob);
        assertTrue(new BCryptPasswordEncoder().matches("newPassword", updatedJob.getPassword()));
    }

    @Test
    void testCreateInvalidJob() {
        Job job = new Job("Job1", "Type1", 99); // Invalid status
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.createJob(job);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());}

}
