import java.util.HashMap;
import java.util.HashSet;

public class Attribute {

	public HashMap<String, Count> attrList = new HashMap<String, Count>();
	public boolean used;
	public int index;
	
	public Attribute(int i) {
		index = i;
	}
	
	public void update() {
		
		attrList = new HashMap<String, Count>();
	}

	public void addPositive(String x) {
		if(attrList.containsKey(x))
			attrList.get(x).positive++;
		else {
			attrList.put(x, new Count());
			attrList.get(x).positive++;
		}
		
	}
	
	public void addNegative(String x) {
		if(attrList.containsKey(x))
			attrList.get(x).negative++;
		else {
			attrList.put(x, new Count());
			attrList.get(x).negative++;
		}
		
	}
	
	

	
}