package com.marchseniy.ZNAUroyaleBot.database.repositories;

import com.marchseniy.ZNAUroyaleBot.database.entitys.UnassignedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnassignedUserRepository extends JpaRepository<UnassignedUser, Long> {
}
