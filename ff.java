import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ff{

	public ArrayList<Servers> serverList = new ArrayList<Servers>();
	public Servers biggestServer;
	public int coreCount = -1;
	public Servers big1;
	public Servers bf1;
	public Servers wf1;
	public Servers ff1;
	
	public ff(){
	
	}
	
	
	//read msg from server, return as string
	
	public String readMsg(byte[] b, BufferedInputStream bis){
		try{
			bis.read(b);
			String str = new String(b, StandardCharsets.UTF_8);
			return str;
		}
		catch (Exception e){
			
		}
		return "error found";
	}
	
	public static void main(String args[]){
		try{
		Socket s = new Socket("localhost",50000);	
		DataOutputStream dout = new DataOutputStream(s.getOutputStream());
		DataInputStream din = new DataInputStream(s.getInputStream());
		BufferedInputStream bin = new BufferedInputStream(din);
		BufferedOutputStream bout = new BufferedOutputStream(dout);
		String serverOutput;
		String input;

		Thread.sleep(100);	
		
		ff cl2 = new ff();	
		
		input = "HELO";
		byte[] inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();
		
		String reply = cl2.readMsg(new byte[32], bin);
		//System.out.println("response to HELO: " + reply);

		input = "AUTH "+System.getProperty("user.name");
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();

		String reply2 = cl2.readMsg(new byte[32], bin);
                //System.out.println("response to AUTH: " + reply2);

		input = "REDY";
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();

		String reply3 = cl2.readMsg(new byte[32], bin);
                System.out.println("response to REDY: " + reply3);

		String[] readlines=reply3.split(" ");
		String core=readlines[4];
		String memory=readlines[5];
		String disk=readlines[6].replace("\n","");
	
		input = "GETS Capable" + " "+ core +" "+ memory +" "+ disk;
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();
		
		
		String reply4 = cl2.readMsg(new byte[32], bin);
	        //System.out.println("response to 'GETS All': " + reply4);
		
		input = "OK";
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();

		//String[] splitter = reply4.split(" ");
		String reply5 = cl2.readMsg(new byte[100000], bin);
		String[] ArrayString = reply5.split("\n");
		
		String[] reads=reply3.split(" ");
		String cor=reads[4];
		String memo=reads[5];
		String dis=reads[6].replace("\n","");
		int jobcore=Integer.parseInt(reads[4]);
		String jid=reads[2];
		String ffserver = "";
		System.out.println("they r "+cor+" "+memo+" "+dis);
		
		if(jobcore==4){
					String[] eachReply1= ArrayString[2].split(" ");
					ffserver= eachReply1[0]+" "+"0";
				}else if(jobcore==2){
					String[] eachReply1= ArrayString[0].split(" ");
					String[] eachReply2= ArrayString[1].split(" ");
										
					if(eachReply1[2].equals("inactive")){
						ffserver=eachReply1[0]+" "+"0";
					}else if(eachReply2[2].equals("inactive")){
						ffserver=eachReply2[0]+" "+"0";
					}else{ffserver=eachReply1[0]+" "+"0";}
				}else if(jobcore==1){
					String[] eachReply1= ArrayString[0].split(" ");
					String[] eachReply2= ArrayString[1].split(" ");
					String[] eachReply3=ArrayString[2].split(" ");
					System.out.println("epy1[2] is "+eachReply1[2]);
					System.out.println("epy2[2] is "+eachReply2[2]);
					System.out.println("epy3[2] is "+eachReply3[2]);
					String v=eachReply2[2];
					System.out.println(v.equals("inactive"));
					if(eachReply1[2].equals("inactive")){
						ffserver=eachReply1[0]+" "+"0";
						System.out.println("is "+ffserver);
					}else if(eachReply2[2].equals("inactive")){
						ffserver=eachReply2[0]+" "+"0";
						System.out.println("is "+ffserver);
					}else if(eachReply3[2].equals("inactive")){
						ffserver=eachReply3[0]+" "+"0";
						System.out.println("is "+ffserver);
					}else{ffserver=eachReply1[0]+" "+"0"; 
					System.out.println("is "+ffserver);
					}
				}
		
		input = "OK";
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();
		
		String replydot = cl2.readMsg(new byte[1], bin);

		
		input = "SCHD "+0+" "+ffserver;
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();
		System.out.println("here");
		String replyjobs = cl2.readMsg(new byte[120], bin);
		int i =1;
		
		input = "REDY";
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();
		
		
		while(true){
			replyjobs = cl2.readMsg(new byte[120], bin);
			String replytype = replyjobs.substring(0,4);
			
			if(replytype.equals("JCPL")){
				input = "REDY";
				inputArr = input.getBytes();
				dout.write(inputArr);
				dout.flush();
				
			
			}
			
			else if(replytype.equals("JOBN")){
				String[] read=replyjobs.split(" ");
				String cores=read[4];
				String memorys=read[5];
				String disks=read[6].replace("\n","");
				String wfserver="";
			
				input = "GETS Capable" + " "+ cores +" "+ memorys +" "+ disks;
				inputArr = input.getBytes();
				dout.write(inputArr);
				dout.flush();
				
				input = "OK";
				inputArr = input.getBytes();
				dout.write(inputArr);
				dout.flush();
				
				//ini jobid and jobcore
				String jobID=read[2];
				int jobCore=Integer.parseInt(read[4]);
				//System.out.println("jbc is"+jobCore);
				

				//get three servers info in replys
				String replys = cl2.readMsg(new byte[100000], bin);
				String[] ArrayStrings = replys.split("\n");
				if(jobCore==4){
					String[] eachReply1= ArrayStrings[2].split(" ");
					wfserver= eachReply1[0]+" "+"0";
				}else if(jobCore==2){
					String[] eachReply1= ArrayStrings[0].split(" ");
					String[] eachReply2= ArrayStrings[1].split(" ");
										
					if(eachReply1[2].equals("inactive")){
						wfserver=eachReply1[0]+" "+"0";
					}else if(eachReply2[2].equals("inactive")){
						wfserver=eachReply2[0]+" "+"0";
					}else{wfserver=eachReply1[0]+" "+"0";}
				}else if(jobCore==1){
					String[] eachReply1= ArrayStrings[0].split(" ");
					String[] eachReply2= ArrayStrings[1].split(" ");
					String[] eachReply3=ArrayStrings[2].split(" ");
					System.out.println("epy1[2] is "+eachReply1[4]);
					System.out.println("epy2[2] is "+eachReply2[2]);
					System.out.println("epy3[2] is "+eachReply3[2]);
					if(eachReply1[2].equals("inactive")){
						wfserver=eachReply1[0]+" "+"0";
					}else if(eachReply2[2].equals("inactive")){
	System.out.println("2 is "+eachReply2[0]+" "+eachReply2[1]+" "+eachReply2[2]+" ");
						wfserver=eachReply2[0]+" "+"0";
						System.out.println("wfs is"+wfserver);
					}else if(eachReply3[2].equals("inactive")){
						wfserver=eachReply3[0]+" "+"0";
					}else{wfserver=eachReply1[0]+" "+"0"; 
					}
				}

				input = "OK";
				inputArr = input.getBytes();
				dout.write(inputArr);
				dout.flush();
					
		
				input = "SCHD " + jobID +" " + wfserver;
				inputArr = input.getBytes();
				dout.write(inputArr);
				dout.flush();
				i++;	
				
			}

			else if(replytype.equals("NONE")){
				break;
			}
			else{					
				input = "REDY";
				inputArr = input.getBytes();
				dout.write(inputArr);
				dout.flush();
				
			}	
		}
		
		input = "QUIT";
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();
		
			
		
	}
	catch(Exception e){
	}
	}
}
