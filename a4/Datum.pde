public class Datum {
	public final String time;	
	public final String destIP;	
	public final String sourceIP;
	public final String destPort;
	public final String operation;
	public final String priority;
	public final String protocol;

	private boolean selected = false;

	public Datum (String time, String destIP, String sourceIP, 
		String destPort, String operation, String priority, String protocol) {	

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

}
