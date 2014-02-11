package com.BrotherOfLewis.SearchPartyPocket.Models;

import com.BrotherOfLewis.SearchPartyPocket.vos.QueryVO;
import java.util.ArrayList;

/**
 * Created by pye on 7/8/13.
 */
public class Game {
    private ArrayList<QueryVO> queries;

    public ArrayList<QueryVO> getQueries() {
        return queries;
    }

    public void setQueries(ArrayList<QueryVO> queries) {
        this.queries = queries;
    }
}
