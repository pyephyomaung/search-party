package com.BrotherOfLewis.SearchPartyPocket.Models;

import com.BrotherOfLewis.SearchPartyPocket.R;

/**
 * Created by Pye on 9/14/13.
 */
public class Player {
    private int id;
    private String description;
    private int correctCount;
    private int backgroundId;

    public Player(int id)
    {
        this.id = id;
        this.setCorrectCount(0);
    }

    public int getId() {
        return id;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public void incrementCorrectCount(int i) {
        this.correctCount++;
    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
