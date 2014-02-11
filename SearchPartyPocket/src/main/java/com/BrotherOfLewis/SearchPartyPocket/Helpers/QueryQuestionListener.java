package com.BrotherOfLewis.SearchPartyPocket.Helpers;

import com.BrotherOfLewis.SearchPartyPocket.Models.QueryQuestion;

public interface QueryQuestionListener {
    public void OnQueryQuestionReturn(QueryQuestion queryQuestion);

    public void OnCacheQueryQuestionCompleted();
}
