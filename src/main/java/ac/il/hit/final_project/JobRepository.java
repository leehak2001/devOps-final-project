package ac.il.hit.final_project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
/* the layer conecting to the db
 * extendes JpaRepository that comunicates with hibernet
 * the code is written in java and automaticliy translated into sql querry by the JPA
 */
public interface JobRepository extends JpaRepository<Job,Long> {

    //find jobs by status
    List<Job> findByStatus(Integer status);

    //find jobs by jobType
    List<Job> findByJobType(String jobType);

    /*explention for find jobs by date rang:
     *@Query: JPQL query-used for querying data from the Java Persistence API (JPA) entity objects
     *  selects all Job entities and filters jobs where the createdAt field is between the startDate and endDate parameters.
     *  :startDate and :endDate are placeholders for the parameters passed to the query.
     *@Param: annotations map the method parameters startDate and endDate to the query parameters :startDate and :endDate.
     */
    @Query("SELECT j FROM Job j WHERE j.createdAt BETWEEN :startDate AND :endDate")
    List<Job> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    /*
     * esentially we translte an object query using jpa into a sql query,
     * the sql query is being runed on the db
     * resulting in a "list" of matching rows in the db,
     * and using the jpa, we "translate" those rows back into objects
     */
}
