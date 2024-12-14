package com.duantn.be_project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from User u where u.email like ?1 ")
    public User findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            select u from User u where (u.email like ?1 and u.fullname like ?2 and u.rolePermission.role.namerole like ?3) and u.rolePermission.role.namerole not like 'Admin'
            """)
    Page<User> listUser(String email, String fullname, String roleName, Pageable pageable);

    @Query("""
            select u from User u where (u.email like ?1 and u.fullname like ?2) and u.rolePermission.role.namerole like 'Admin'
            """)
    Page<User> listUserAdmin(String email, String fullname, Pageable pageable);

    @Query("""
        select u from User u where (u.rolePermission.permission.id = ?1) and u.rolePermission.role.namerole like 'Admin'
        """)
Page<User> listUserAdminByIdPermission(Integer idPermission, Pageable pageable);
}
