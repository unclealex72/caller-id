package uk.co.unclealex.callerid.server.model;

import uk.co.unclealex.hibernate.model.KeyedBean;

public abstract class BusinessKeyedBean<M extends BusinessKeyedBean<M, K>, K extends Comparable<K>> extends KeyedBean<M> {

	public abstract K getBusinessKey();
	
	@Override
	public String toString() {
		return getBusinessKey().toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean equals(Object obj) {
		return obj != null && getClass().equals(obj.getClass()) && compareTo((M) obj) == 0;
	}
	
	public final int compareTo(M o) {
		return getBusinessKey().compareTo(o.getBusinessKey());
	}
	
	@Override
	public final int hashCode() {
		return getBusinessKey().hashCode();
	}
}
