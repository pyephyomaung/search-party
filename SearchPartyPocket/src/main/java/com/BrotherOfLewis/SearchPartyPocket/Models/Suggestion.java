package com.BrotherOfLewis.SearchPartyPocket.Models;

public class Suggestion {
    private String theSuggestion;
    private int theQueries;
    private boolean theBest;

    public Suggestion(String suggestionData, int queriesNumInt) {
        this.theSuggestion = suggestionData;
        this.theQueries = queriesNumInt;
    }

    public String getTheSuggestion() {
        return theSuggestion;
    }

    public void setTheSuggestion(String theSuggestion) {
        this.theSuggestion = theSuggestion;
    }

    public int getTheQueries() {
        return theQueries;
    }

    public void setTheQueries(int theQueries) {
        this.theQueries = theQueries;
    }

    public boolean isTheBest() {
        return theBest;
    }

    public void setTheBest(boolean theBest) {
        this.theBest = theBest;
    }
}