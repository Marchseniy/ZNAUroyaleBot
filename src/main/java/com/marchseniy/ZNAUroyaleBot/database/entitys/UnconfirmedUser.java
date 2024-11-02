package com.marchseniy.ZNAUroyaleBot.database.entitys;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "unconfirmed_user_data")
public class UnconfirmedUser {
    @Id
    private long id;
    private long chatId;
    @Column(length = 10)
    private String tag;
    private String name;
    private List<String> expectedDeckCards;
    private LocalDateTime expirationTime;
    private String firstSwappedCard;
    private String secondSwappedCard;
}
