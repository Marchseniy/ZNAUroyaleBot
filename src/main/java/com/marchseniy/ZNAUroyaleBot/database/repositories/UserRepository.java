package com.marchseniy.ZNAUroyaleBot.database.repositories;

import com.marchseniy.ZNAUroyaleBot.database.entitys.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
