package ac.il.hit.final_project;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest //It sets up the entire Spring environment for your test, including configuration, beans, and dependencies.
public class JobServiceTest {

    private static final Logger log = LoggerFactory.getLogger(JobServiceTest.class);
    /*@Autowired marks for Spring to creaet and inject the object to the correct place
         automaticliy, meaning it is responisble for lifecycle management- we do not need to do:
            jobRepository = new JobRepository();
            jobService = new JobService(jobRepository);
          nor do we need teardown!!
         */
    @Autowired
    private JobService jobService;

    @Autowired
    private JobRepository jobRepository;

    private static final Logger logger = LoggerFactory.getLogger(JobServiceTest.class);

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        jobRepository.deleteAll();
    }

    @Nested
    class createTests {
        // Standard Unit Tests (@Test):

        //testAddJob() from project file
        @Test
        void testAddJob() {
            Job job = new Job("Job1", "Type1", 200);

            Job savedJob = jobService.saveJob(job);

            assertEquals(job.getJobName(), savedJob.getJobName());
            assertEquals(job.getJobType(), savedJob.getJobType());
            assertEquals(job.getStatus(), savedJob.getStatus());
        }

        @Test
        void testAddJobWithPassword() {
            String pasword = "password123";
            Job job = new Job("Job1", "Type1", 200, pasword);

            Job savedJob = jobService.saveJob(job);

            assertEquals(job.getJobName(), savedJob.getJobName());
            assertEquals(job.getJobType(), savedJob.getJobType());
            assertEquals(job.getStatus(), savedJob.getStatus());
            assertTrue(passwordEncoder.matches(pasword, savedJob.getPassword()));
        }


        // testAddJobWithVariousStatuses() from project file
        @ParameterizedTest
        @ValueSource(ints = {200, 400, 404})
        void testAddJobWithVariousStatuses(int status) {
            Job job = new Job("Job1", "Type1", status);

            Job savedJob = jobService.saveJob(job);

            assertEquals(status, savedJob.getStatus());
        }

        @Test
        void testSaveJobWithInvalidData() {
            Job job = new Job("Job1", "Type1", 2000);

            assertThrows(RuntimeException.class, () -> jobService.saveJob(job));
        }

    }


    @Nested
    class readTests {

        //testGetJob from project file
        @Test
        void testGetJobById() {
            Job job = new Job("Job1", "Type1", 200);
            Job savedJob = jobService.saveJob(job);

            Optional<Job> foundJob = jobService.findJobById(savedJob.getId());

            assertTrue(foundJob.isPresent());
            assertEquals(savedJob.getId(), foundJob.get().getId());
        }

        @Test
        void testGetJobByType() {
            String type="Type1";
            Job job = new Job("Job1",type , 200);
            Job savedJob = jobService.saveJob(job);

            List<Job> foundJob = jobService.findJobByType(type);

            assertFalse(foundJob.isEmpty());
            assertEquals(type, foundJob.get(0).getJobType());
        }


        //testGetJobByDifferentIds from project file
        @ParameterizedTest
        @ValueSource(longs = {9L, 10L, 11L})
        void testGetJobByDifferentIds(long id) {
            Job job = new Job("Job" + id ,"Type1", 200);
            jobService.saveJob(job);

            System.out.println("job"+id+": "+ job.getId());

            Optional<Job> foundJob = jobService.findJobById(id);

            assertTrue(foundJob.isPresent());
            assertEquals(id, foundJob.get().getId());
        }
    }

    @Nested
    class UpdateTests {

        @Test
        void testUpdateJobPassword() {
            Job job = new Job("Job1", "Type1", 200, "oldPassword");
            Job savedJob = jobService.saveJob(job);

            Job updatedJob = jobService.updateJobPassword(savedJob.getId(), "oldPassword", "newPassword");

            boolean matches = passwordEncoder.matches("newPassword", updatedJob.getPassword());

            assertTrue(matches);
        }

        @Test
        void testUpdateJobStatus() {
            Job job = new Job("Job1", "Type1", 400);
            Job savedJob = jobService.saveJob(job);
            logger.info("Updated at creation Job: {}", savedJob.getUpdatedAt());

            Job updatedJob = jobService.updateJobStatus(savedJob.getId(), 200);

            assertEquals(200, updatedJob.getStatus());

            logger.info("Updated at after change: {}", updatedJob.getUpdatedAt());
        }

        @Test
        void testUpdateJobWithInvalidData() {
            Job job = new Job("Job1", "Type1", 400);
            Job savedJob = jobService.saveJob(job);

            Job jobDetails = new Job("InvalidJob", "InvalidType", 1000);

            assertThrows(RuntimeException.class, () -> jobService.updateJob(job.getId(), jobDetails));
        }
    }

    @Nested
    class deleteTests {

        @Test
        void testDeleteJob() {
            Job job = new Job("Job1", "Type1", 200);
            Job savedJob = jobService.saveJob(job);

            jobService.deleteJob(savedJob.getId());

            Optional<Job> foundJob = jobService.findJobById(savedJob.getId());

            assertFalse(foundJob.isPresent());
        }

        // Exception Tests
        @Test
        void testDeleteNonExistentJob() {
            Long jobId = 999L;

            assertThrows(RuntimeException.class, () -> jobService.deleteJob(jobId));
        }
    }
}