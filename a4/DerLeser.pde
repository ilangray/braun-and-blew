public class DerLeser {
	private final String fileName;

	public DerLeser (String fileName) {
		this.fileName = fileName;
	}

	public ArrayList<Datum> readIn() {
		ArrayList<Datum> toReturn = new ArrayList<Datum>();
		String[] lines = loadStrings(fileName);

		for (String l : lines) {
			if (l.startsWith("Time")) {  // Header
				continue;
			}

			toReturn.add(createDatum(l));
		}

		return toReturn;
	}


	// Takes in a string that is comma-separated Datum and makes Datum
	private Datum createDatum(String l) {
		String[] listL = split(l, ",");

		return new Datum(listL[0], listL[3], listL[1], listL[4], 
			listL[6], listL[5], listL[7]);
	}

	public void tPrintOne(ArrayList<Datum> d) {
		Datum dat = d.get(0);
		println("time = " + dat.time);
		println("destIP = " + dat.destIP);
		println("sourceIP = " + dat.sourceIP);
		println("destPort = " + dat.destPort);
		println("operation = " + dat.operation);
		println("priority = " + dat.priority);
		println("protocol = " + dat.protocol);
	}

}