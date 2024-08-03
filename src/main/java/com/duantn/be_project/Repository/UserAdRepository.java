package com.duantn.be_project.Repository;

import com.duantn.be_project.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserAdRepository extends CrudRepository<User, Integer> {
    
    @Query(value = "SELECT YEAR(s.createdtime) AS Year, COUNT(DISTINCT u.id) AS TotalUsers " +
                   "FROM Stores s " +
                   "INNER JOIN Users u ON u.id = s.userid " +
                   "GROUP BY YEAR(s.createdtime) " +
                   "ORDER BY Year", nativeQuery = true)
    List<Map<String, Object>> findUsersByYear();
}
