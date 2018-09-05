import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import org.w3c.dom.Attr;

import java.lang.Math;

public class C45 {

	public static int corr=0; 
	
	public static int incorr=0; 
	
	public static ArrayList<Attribute> attributes = new ArrayList<Attribute>(); 
	
	public static ArrayList<String[]> records = new ArrayList<String[]>(); 
	
	public static String pos = "e"; 
	
	public static String neg = "p";
	
	private static String trainingFile;
	
	private static String testFile;
	
	private static String outputFile;
	
	
	public static void main(String[] args) throws IOException {
		
		
		trainingFile = args[0]; testFile = args[1]; outputFile = args[2]; 
		create(); 
		load(); 
		
		
		final PrintStream oldStdout = System.out;
		
		Node root = new Node("root"); 
		root.recordList = records;
		root.attachedAttr = null;
		
		split(root); 
		treevis(root); 

		System.setOut(new PrintStream(new FileOutputStream(outputFile)));
		tester(root); 
		
		System.out.println("Accuracy is: "+ 100*(int)corr/(int)(incorr+corr) + "%");
		System.setOut(oldStdout);
		System.out.println("Accuracy is: "+ 100*(int)corr/(int)(incorr+corr) + "%");
	  	
		}

	

	private static void treevis(Node root) {
		LinkedList<Node> leaf = new LinkedList<Node>();
		
		leaf.add(root);
		while(!leaf.isEmpty()) {
			Node a = leaf.poll();
			if(a.attachedAttr!=null) {
				System.out.println("Splitting attribute: " +a.attachedAttr.index);
				System.out.println("Attribute: "+ a.name+" Edible:(" + a.attachedAttr.attrList.get(a.name).positive + ") Poison:(" + a.attachedAttr.attrList.get(a.name).negative+")");
				System.out.println();
			}
			
			if(a.children.size()!=0)
				leaf.addAll(a.children);
		}
		
		
	}

	

	private static void tester(Node root) throws IOException {
		ArrayList<String[]> rec = new ArrayList<String[]>();
		Scanner test = new Scanner(new File(testFile));
		while(test.hasNextLine()) {
			String header = test.nextLine();
			String testrec[]  = header.split("	");
			rec.add(testrec);
			
			
			
		}
		root.testList.addAll(rec);

		classifier(root);

		test.close();
		
	}

	private static void classifier(Node root) {

		
		if(root.children.size()==0) {
			
			for(String[] rec: root.testList) {

				if(root.rule!=null) {
					if(rec[0].equals(root.rule)) {
						System.out.println(Arrays.toString(rec) + " Correct classification label: "+root.rule);
						corr++;
					}
					else {
						System.out.println(Arrays.toString(rec) + " Incorrect classification label: "+root.rule);
						incorr++;
					}
				}
				else {
					corr++;
					System.out.println(Arrays.toString(rec) + " Classification label: Impure class");
				}
					
			}
			return;
		}
		

			for(String[] rec: root.testList) {
				for(Node element : root.children) {
					if(rec[element.attachedAttr.index].equals(element.name)) {
						element.testList.add(rec);
					}
				
				}
				
			}
		
		for(Node element : root.children)
			classifier(element);
		
	}
	

	private static void split(Node root) {
	
		boolean flag=false;
		for(Attribute attr : attributes) {
			if(attr.used==false)
				flag = true;
		}
		if(!flag)
			return;
		
		
		int p=0; 
		int e=0;
		
		if(!root.name.equals("root")) {
			for(Attribute attr:attributes) {
				if(!attr.used)
					attr.update();
			}
		}
		
		for(String[] rec : root.recordList) {
			if(!root.name.equals("root")) {
				
					for(int i=1; i<rec.length; i++) {
						if(rec[0].equals(pos)) {
							if(attributes.get(i-1).used==false)
								attributes.get(i-1).addPositive(rec[i]);
							}
						else {
							if(attributes.get(i-1).used==false)
								attributes.get(i-1).addNegative(rec[i]);
							}
						}
							
					}
			
			if(rec[0].equals(pos)) {
				e++;
			}
			else
				p++;
		}
		
		
		
		if(p==0) {
			root.rule = pos;
			return;
		}
		
		if(e==0) {
			root.rule = neg;
			return;
		}
			
		Attribute bestatt = selectAttribute(e, p, root.recordList); 

		if(bestatt==null)
			return;
		else
			bestatt.used=true;
		

		
		for(String key : bestatt.attrList.keySet()) { 
			Node child = new Node(key);
			child.attachedAttr = bestatt;
			root.children.add(child);
		}
			
	
		for(String[] rec : root.recordList) {
			for(Node child : root.children) {
				if(rec[bestatt.index].equals(child.name)) {
					child.recordList.add(rec);
					
				}
			}
		}
		
		for(Node child : root.children) {
			split(child);
		}
				
	}

	


	private static double info(double e, double p) {
		return (-1 * e / (e+p)) * (Math.log(e / (e+p)) / Math.log(2))  + (-1 * p / (e+p)) * (Math.log(p / (e+p)) / Math.log(2));
		
	}


	
	private static Attribute selectAttribute(int e, int p, ArrayList<String[]> recordList) {
		
		double info = info(e, p);
		double max = -Double.MIN_VALUE;
		
		Attribute bestatt = null;

		for(Attribute attr : attributes) {
				
			double attrInfo = 0.0; 
			
			double splitInfo = 0.0; 
			
			
			if(attr.used)
				continue;
			
			
			
			for(String key : attr.attrList.keySet()) {
				
				double a = attr.attrList.get(key).positive;
				double b = attr.attrList.get(key).negative;
				if(b==0) b++; 
				if(a==0) a++;
				
				attrInfo += (a/(double)recordList.size()) * info(a,b);

				splitInfo += ((-a/(a+b)) *(Math.log(a/(a+b))     /      Math.log(2)   )    );
				
			}
			

			
			
			double gain =(info-attrInfo);
			double gainRatio = gain/splitInfo;
		
			if( gainRatio >= max) {
				max = gainRatio;
				bestatt = attr; 
			}
			
			
		}
		return bestatt;
	}




	private static void load() throws IOException {
		
		Scanner scan = new Scanner(new File(trainingFile));
		while(scan.hasNextLine()) {
			String headerLine = scan.nextLine();
			String record[]  = headerLine.split("	");
			
				for(int i=1; i<record.length; i++) {
					if(record[0].equals(pos)) 
						attributes.get(i-1).addPositive(record[i]);
					
					else
						attributes.get(i-1).addNegative(record[i]);
				}
				
			records.add(record);
		}
		scan.close();
	}

	
	private static void create() throws IOException {

		Scanner scan = new Scanner(new File(trainingFile));
		
		String attr = scan.nextLine();
		String attrList[] = attr.split("	");
		
		for(int i=1; i<attrList.length; i++) {
			Attribute attribute = new Attribute(i);
			attributes.add(attribute);
		}
		
		scan.close();
		
	}

}