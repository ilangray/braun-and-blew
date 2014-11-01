
import java.util.*;

class DatumGrid {
	private final Map<String, ArrayList<Datum>> data;

	private final int w;
	private final int h;

	public DatumGrid(int w, int h){
		this.w = w;
		this.h = h;
		this.data = new HashMap<String, ArrayList<Datum>>();
	}

	private String getKey(int col, int row) {
		return col + "," + row;
	}

	public void put(int col, int row, ArrayList<Datum> elem) {
		data.put(getKey(col, row), elem);
	}

	public ArrayList<Datum> get(int col, int row){
		return data.get(getKey(col, row));
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}
}