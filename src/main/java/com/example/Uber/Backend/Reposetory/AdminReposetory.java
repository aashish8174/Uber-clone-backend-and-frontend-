package com.example.Uber.Backend.Reposetory;

import com.example.Uber.Backend.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminReposetory extends JpaRepository<Admin, Integer> {

}