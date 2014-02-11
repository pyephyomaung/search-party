package com.BrotherOfLewis.SearchPartyPocket.vos;

/**
 * Created by pye on 7/8/13.
 */
public class QueryVO extends SimpleObservable<QueryVO> {
    private int id = -1;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
        notifyObservers(this);
    }

    private String query = "";
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
        notifyObservers(this);
    }

    private String pack = "";
    public String getPack() {
        return pack;
    }
    public void setPack(String pack) {
        this.pack = pack;
        notifyObservers(this);
    }

    @Override
    synchronized public QueryVO clone() {
        QueryVO vo = new QueryVO();
        vo.setId(id);
        vo.setQuery(query);
        vo.setPack(pack);
        return vo;
    }

    synchronized public void consume(QueryVO vo) {
        this.id = vo.getId();
        this.query = vo.getQuery();
        this.pack = vo.getPack();
        notifyObservers(this);
    }
}
