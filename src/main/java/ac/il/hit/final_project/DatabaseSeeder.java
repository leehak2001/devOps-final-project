package ac.il.hit.final_project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;


// defines a Spring bean and a CommandLineRunner that initializes a database using a JpaRepository
@Configuration //It defines a configuration class that contains bean definitions
public class DatabaseSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    /*
     * bean is an object that is managed by Spring ( for creating, configuring, and managing the lifecycle)
     * Create a Spring bean that implements CommandLineRunner
     * CommandLineRunner- Spring interface for classes that should run when the application starts.
     * Inject a JpaRepository into this bean
     */
    @Bean
    CommandLineRunner initDatabase(JpaRepository jobRepository) {
        return args -> {
            logger.info("Seeding database with initial data...");

            //String jobName,String jobType, String status
            Job job1 = new Job("job numer 1", "regular", 200, "111");
            Job job2 = new Job("job numer 2", "error", 404, "222");
            Job job3 = new Job("job numer 3", "creation", 204, "333");

            jobRepository.save(job1);
            logger.info("Created Job: {}", job1);

            jobRepository.save(job2);
            logger.info("Created Job: {}", job2);

            jobRepository.save(job3);
            logger.info("Created Job: {}", job3);

            logger.info("Database seeding completed.");

        };
    }
}