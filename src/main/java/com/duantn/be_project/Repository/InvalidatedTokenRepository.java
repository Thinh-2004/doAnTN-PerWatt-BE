package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Request_Response.authenticate.InvalidatedToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Lazy
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
