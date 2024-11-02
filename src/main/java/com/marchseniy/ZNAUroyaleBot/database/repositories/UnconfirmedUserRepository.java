package com.marchseniy.ZNAUroyaleBot.database.repositories;

import com.marchseniy.ZNAUroyaleBot.database.entitys.UnconfirmedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnconfirmedUserRepository extends JpaRepository<UnconfirmedUser, Long> {
}
