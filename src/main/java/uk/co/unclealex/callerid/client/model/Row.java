package uk.co.unclealex.callerid.client.model;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class Row {

	private final List<Object> i_cells = Lists.newArrayList();
	private final String i_typeMarker;
	
	public Row() {
		this(null);
	}
	
	public Row(String typeMarker) {
		super();
		i_typeMarker = typeMarker;
	}

	public Row(String typeMarker, Object... cells) {
		this(typeMarker);
		getCells().addAll(Arrays.asList(cells));
	}

	public List<Object> getCells() {
		return i_cells;
	}
	
	public String getTypeMarker() {
		return i_typeMarker;
	}
}
