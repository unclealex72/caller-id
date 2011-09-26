package uk.co.unclealex.callerid.client.model;

import java.util.List;

import com.google.common.collect.Lists;

public class Table {

	private final List<Row> i_rows = Lists.newArrayList();

	public Table() {
		super();
	}
	
	public Table addRow(Row row) {
		getRows().add(row);
		return this;
	}
	
	public Table addRow(String typeMarker, Object... cells) {
		return addRow(new Row(typeMarker, cells));
	}
	
	public Object getCellAt(int row, int column) {
		Object cell = null;
		List<Row> rows = getRows();
		if (row < 0 || row >= rows.size()) {
			cell = null;
		}
		else {
			List<Object> cells = rows.get(row).getCells();
			if (column < 0 || column >= cells.size()) {
				cell = null;
			}
			else {
				cell = cells.get(column);
			}
		}
		return cell;
	}
	
	public List<Row> getRows() {
		return i_rows;
	}
}
