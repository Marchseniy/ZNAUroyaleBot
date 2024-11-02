package com.marchseniy.ZNAUroyaleBot.database.entitys;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "unassigned_user_data")
public class UnassignedUser {
    @Id
    private long id;
    private LocalDateTime expirationTime;
}