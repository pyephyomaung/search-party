package com.BrotherOfLewis.SearchPartyPocket.vos;


public interface EasyObservable<T> {
	
	void addListener(OnChangeListener<T> listener);
	void removeListener(OnChangeListener<T> listener);
	
}