public class Datum {
	public final int id;
	public final String time;	
	public final String destIP;	
	public final String sourceIP;
	public final String destPort;
	public final String operation;
	public final String priority;
	public final String protocol;
	private boolean marked = false;

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

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean m) {
		marked = m;
	}

}
