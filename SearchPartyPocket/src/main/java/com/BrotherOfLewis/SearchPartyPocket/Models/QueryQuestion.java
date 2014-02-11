package com.BrotherOfLewis.SearchPartyPocket.Models;

/**
 * Created by Pye on 8/10/13.
 */
public class QueryQuestion {
    private String query;
    private Suggestion[] suggestions;

    public QueryQuestion(String query, Suggestion[] suggestions)
    {
        this.query = query;
        this.suggestions = suggestions;
    }

    public String getQuery() {
        return query;
    }

    public Suggestion[] getSuggestions() {
        return suggestions;
    }
}
