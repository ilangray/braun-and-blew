public class Datum {

	// the names of datum properties
	public static final String TIME = "time";
	public static final String DEST_IP = "destIP";
	public static final String SOURCE_IP = "sourceIP";
	public static final String DEST_PORT = "destPort";
	public static final String OPERATION = "operation";
	public static final String PRIORITY = "priority";
	public static final String PROTOCOL = "protocol";

	public final int id;
	public final String time;	
	public final String destIP;	
	public final String sourceIP;
	public final String destPort;
	public final String operation;
	public final String priority;
	public final String protocol;

	private boolean selected = false;

	public Datum (int id, String time, String destIP, String sourceIP, 
		String destPort, String operation, String priority, String protocol) {	

		this.id = id;
		this.time = time;	
		this.destIP = destIP;	
		this.sourceIP = sourceIP;
		this.destPort = destPort;
		this.operation = operation;
		this.priority = priority;
		this.protocol = protocol;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean s) {
		selected = s;
	}

	// property should be on of the constants defined above: TIME, DEST_IP, etc
	public String getValue(String property) {
		if (property == null) {
			throw new IllegalArgumentException("Cannot retrieve datum's value for null property");
		}

		if (property.equals(TIME)) {
			return time;
		}
		if (property.equals(DEST_IP)) {
			return destIP;
		}
		if (property.equals(SOURCE_IP)) {
			return sourceIP;
		}
		if (property.equals(DEST_PORT)) {
			return destPort;
		}
		if (property.equals(OPERATION)) {
			return operation;
		}
		if (property.equals(PRIORITY)) {
			return priority;
		}
		if (property.equals(PROTOCOL)) {
			return protocol;
		}

		throw new IllegalArgumentException("Unknown datum property = " + property);
	}

}
