package ac.il.hit.final_project;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

/**
 * Job entity class.
 * This class is mapped to a database table named app_job.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "app_job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //automaticliy generated id by the db deafult method
    private Long id;
    private String jobName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String jobType;
    private String password;

    //constractur without password
    public Job(String jobName, String jobType, Integer status) {
        this(jobName, jobType, status, "");
    }

    //constractor with password
    public Job(String jobName, String jobType, Integer status, String password) {
        this.jobName = jobName;
        this.jobType = jobType;
        //this.setStatus(status);
        this.status=status;
        this.setPassword(password);

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public void setPassword(String password) {
        final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    /*
    public void setStatus(Integer status){
        if(status<100 || status>999){
            throw new RuntimeException("Illegal status");}
        else this.status=status;
    }*/

    @PreUpdate //is invoked before an entity is updated in the database
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

