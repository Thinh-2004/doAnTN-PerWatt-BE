package com.duantn.be_project.Repository;

import com.duantn.be_project.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserAdRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT COUNT(id) AS TotalUsers FROM Users", nativeQuery = true)
    List<Map<String, Object>> findTotalUsers();

}
