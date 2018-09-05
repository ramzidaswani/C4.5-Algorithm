import java.util.ArrayList;

public class Node {

	public ArrayList<Node> children = new ArrayList<Node>();
	public String rule;
	public String name;
	public Attribute attachedAttr;
	
	public ArrayList<String[]> recordList = new ArrayList<String[]>();
	public ArrayList<String[]> testList = new ArrayList<String[]>();
	
	public Node(String name) {
		this.name = name;
	}
	

}