
// buckets datums by a pair of properties
// buckets are indexed by either (col,row) or a
// pair of values for the properties
class Bucketizer{

	private final ArrayList<Datum> data;

	private final String xProperty;
	private final String yProperty;

	private final ArrayList<String> xValues;
	private final ArrayList<String> yValues;

	private final DatumGrid grid;

	private final int maxCount;

	public Bucketizer(ArrayList<Datum> data, String xProperty, String yProperty){
		this.data = data;

		this.xProperty = xProperty;
		this.yProperty = yProperty;

		this.xValues = getUniqueValues(data, xProperty);
		this.yValues = getUniqueValues(data, yProperty);

		// sort x and y values
		Collections.sort(yValues, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return compareRange(s1, s2);
			}
		});
		Collections.sort(xValues, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return compareTimes(s1, s2);
			}
		});

		this.grid = initGrid();
		this.maxCount = computeMaxCount();
	}

	private int compareTimes(String t1, String t2) {
		ArrayList<Integer> times1 = splitTimes(t1);
		ArrayList<Integer> times2 = splitTimes(t2);

		for (int i = 0; i < times1.size(); i++) {
			if (times1.get(i) > times2.get(i)) {
				return 1;
			}
			if (times1.get(i) < times2.get(i)) {
				return -1;
			}
		}
		return 0;
	}

	private ArrayList<Integer> splitTimes(String time) {
		String[] parts = trim(split(time, ":"));

		ArrayList<Integer> ints = new ArrayList<Integer>();

		for (String p : parts) {
			ints.add(Integer.parseInt(p));
		}

		return ints;
	}

	private int compareRange(String r1, String r2) {
		int s1First = Integer.parseInt(trim(split(r1, "-")[0]));
		int s2First = Integer.parseInt(trim(split(r2, "-")[0]));

		return s1First < s2First ? 1 : -1;
	}

	private DatumGrid initGrid() {
		DatumGrid grid = new DatumGrid(xValues.size(), yValues.size());

		// add everything into the grid
		for (Datum d : data) {
			addToGrid(grid, d);
		}

		return grid;
	}

	private void addToGrid(DatumGrid grid, Datum d) {
		// figure out where it should be
		String xValue = d.getValue(xProperty);
		String yValue = d.getValue(yProperty);

		int c = xValues.indexOf(xValue);
		int r = yValues.indexOf(yValue);
		
		// grab the ArrayList thats already there
		ArrayList<Datum> ds = grid.get(c, r);

		// construct if need be
		if (ds == null) {
			ds = new ArrayList<Datum>();
		}

		// add teh datum to the list
		ds.add(d);

		// put the (new) list back in the grid
		grid.put(c, r, ds);
	}

	private int computeMaxCount() {
		int maxCount = 0;
		for (int r = 0; r < grid.getHeight(); r++) {
			for (int c = 0; c < grid.getWidth(); c++) {
				maxCount = Math.max(maxCount, getCount(c, r));
			}
		}
		return maxCount;
	}

	public ArrayList<Datum> getDatums(int col, int row) {
		ArrayList<Datum> ds = grid.get(col, row);

		if (ds == null) {
			return new ArrayList();
		} else {
			return ds;
		}
	}

	public int getCount(int col, int row) {
		return getDatums(col, row).size();
	}

	public int getMaxCount() {
		return maxCount;
	}

	public ArrayList<String> getXValues() {
		return xValues;
	}

	public ArrayList<String> getYValues() {
		return yValues;
	}	

	// returns all of the unique values for the property on the data
	private ArrayList<String> getUniqueValues(ArrayList<Datum> data, String property) {
		HashSet<String> vals = new HashSet<String>();

		for (Datum d : data) {
			vals.add(d.getValue(property));
		}

		return new ArrayList<String>(vals);
	}
}