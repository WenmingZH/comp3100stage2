import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class wf2{

	public ArrayList<Servers> serverList = new ArrayList<Servers>();
	public Servers biggestServer;
	public int coreCount = -1;
	public Servers big1;
	public Servers wf1;

	
	public wf2(){
	
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
		
		wf2 cl2 = new wf2();	
		
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
		int leng=ArrayString.length;
		for(String server: ArrayString){
			String[] singleServer = server.split(" ");
			Servers serverSingle = new Servers();
			serverSingle.serverName = singleServer[0];
			serverSingle.serverId = Integer.parseInt(singleServer[1]);
			serverSingle.state = singleServer[2];
			serverSingle.currStartTime = Integer.parseInt(singleServer[3]);
			serverSingle.cores = Integer.parseInt(singleServer[4]);
			serverSingle.nem = Integer.parseInt(singleServer[5]);
			serverSingle.disk = Integer.parseInt(singleServer[6]);
			cl2.serverList.add(serverSingle);
		} // list of server with array indices as attributes
			
		//System.out.println("response to 'OK': \n" + reply5);
		input = "OK";
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();
		
		String replydot = cl2.readMsg(new byte[1], bin);

		//System.out.println("Reply should be '.': " + replydot);
		for(Servers checkserver: cl2.serverList){
			if(cl2.coreCount < checkserver.cores){
				cl2.biggestServer = checkserver;
			}
			
		}
		
		for(Servers checkserver: cl2.serverList){
			if(checkserver.cores == cl2.biggestServer.cores){
				cl2.big1 = checkserver;
				break;
			}
			
		}

		
		

		String bigserver = cl2.big1.serverName + " " + cl2.big1.serverId;
		//System.out.println("Largest server is: "+ bigserver)
		String bfserver = "";
		String ffserver = "";
		String wfserver = "";
		
		input = "SCHD "+0+" "+bigserver;
		inputArr = input.getBytes();
		dout.write(inputArr);
		dout.flush();

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
				String replyed = cl2.readMsg(new byte[100000], bin);

				String replys = replyed.substring(10,replyed.length()-1);
				//System.out.println("index is "+findIndex);
				//System.out.println("replys is "+replys);


				//String replys0=din.readLine();
				String[] ArrayStrings = replys.split("\n");
				int test=-1;
				for(int j=0;j<ArrayStrings.length;j++){
					if(ArrayStrings[j].split(" ")[2].equals("inactive")){
					wfserver=ArrayStrings[j].split(" ")[0]+" "+"0";
					test++;
					}
				}

				if(test==-1){
					wfserver=ArrayStrings[ArrayStrings.length-1].split(" ")[0]+" "+"0";
					//System.out.println("show is" + ArrayStrings[0]);
					//System.out.println("length is "+ArrayStrings.length);
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
