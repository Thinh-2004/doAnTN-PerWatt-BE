
package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Follow;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Integer>{
    Follow findByUserAndStore(User user, Store store);
    
    int countByStore(Store store);
    
}
