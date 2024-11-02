package com.marchseniy.ZNAUroyaleBot.clashroyale.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ConvertedDeck {
    private List<String> deck;
    private String firstSwappedCard;
    private String secondSwappedCard;
}
