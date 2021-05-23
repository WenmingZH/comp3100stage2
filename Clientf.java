import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Clientf{

	public ArrayList<Servers> serverList = new ArrayList<Servers>();
	public Servers biggestServer;
	public int coreCount = -1;
	public Servers big1;
	
	public Clientf(){
	
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
		
		Clientf cl2 = new Clientf();	
		
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
                //System.out.println("response to REDY: " + reply3);

		input = "GETS All";
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
                                //String namecheck = cl2.biggestServer.serverName;
				//String corecheck = Integer.toString(cl2.biggestServer.cores)
				//System.out.println(namecheck + " " + corecheck);
			}
		}
		
		for(Servers checkserver: cl2.serverList){
			if(checkserver.cores == cl2.biggestServer.cores){
				cl2.big1 = checkserver;
				break;
			}
			
		}
		
		String bigserver = cl2.big1.serverName + " " + cl2.big1.serverId;
		String cap=cl2.big1.cores+" "+cl2.big1.nem+" "+cl2.big1.disk;
		//System.out.println("Largest server is: "+ bigserver)

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
				input = "GETS Capable" + cap;
				inputArr = input.getBytes();
				dout.write(inputArr);
				dout.flush(); 
				/*input = "SCHD " + i +" " + bigserver;
				inputArr = input.getBytes();
				dout.write(inputArr);
				dout.flush();
				i++;*/	
				
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
