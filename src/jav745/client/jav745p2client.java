package jav745.client;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class jav745p2client {


	public static void main(String[] args) throws IOException {

		// ensure that the correct number of parameters were
		// provided
		if (args.length != 4) {
			System.err.println(
					"Usage: java jav745p2client <host name> <port number> <configuration file> <starting balance>");
			System.exit(1);
		}

		// retrieve parameters from the command line
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		Path path = Paths.get(args[2]);
		Account Starting_Balance  = new Account(Double.parseDouble(args[3]));
		int count = 0;
		TicketRequest[] TR = new TicketRequest[100];
		
		
		try (Scanner s = new Scanner(path)) {
			
			while (s.hasNextLine() ) {
				// System.out.println(portNumber);

				String line = s.nextLine();
				
				Scanner data = new Scanner(line);
				data.useDelimiter(",");

				while (data.hasNext()) {

					String Concert = data.next();
					String Venue = data.next();

					List<Integer> SeatList = new LinkedList<>();
					SeatList.add(data.nextInt());
					SeatList.add(data.nextInt());


					String ShowDate = data.next();

					TR[count] = new TicketRequest(Concert, Venue, SeatList,ShowDate);
					count++;

				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		

		ConnectToServer(TR,hostName, portNumber,Starting_Balance,count);
	}

	private static void ConnectToServer(TicketRequest[] tr , String Host, int Port,Account Balance,int c) {
		try (
				// create socket which provides connection to server
				Socket echoSocket = new Socket(Host, Port);
				

				// get access to i/o streams connected to server
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));) 
		{
			
			for(int i =0;i<c;i++)
			{	
				System.out.println("Sending Ticket Request to server...");
				System.out.println(tr[i].getOrder());
				out.println(tr[i].getOrder());
				
				if (in.readLine().equalsIgnoreCase("YES")) 
				{
				System.out.println("Tickets Are Available.");
				String Confirm = "YES";
				System.out.println("Sending tentatively Payment Confirmation.");
				out.println(Confirm);
				}	
				else 
				{System.out.println("Tickets Are Not Available.");
				break;}
				
				Double Payment = Double.parseDouble(in.readLine());
				System.out.printf("\nReady to send %.2f Payment Amount to Server ? (YES/NO) :" , Payment);
				Scanner ans = new Scanner(System.in);
				String Confirm = ans.next();
				out.println(Confirm);
				if(Confirm.equalsIgnoreCase("YES"))
				{Balance.setBalance((Balance.getBalance()-Payment));
				System.out.println("\nNew Account Balance = "+Balance.getBalance());}
				else {System.out.println("\nYou Declined the Transaction!");}
				
			}
			
			System.out.println("\n Processes all Ticket Requests.");
			
			
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + Host);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + Host);
			System.exit(1);
		}

	}

}
class TicketRequest {
	private String Concert;
	private String Venue;
	List<Integer> SeatList = new LinkedList<>();
	private String ShowDate;

	public TicketRequest(String C, String V, List<Integer> s, String d) {
		Concert = C;
		Venue = V;
		SeatList = s;
		ShowDate = d;

	}
	
	String getConcert()
	{
		return Concert;
	}
	String getVenue()
	{
		return Venue;
	}
	List<Integer> getSeats()
	{
		return SeatList;
	}
	String getDate()
	{
		return ShowDate;
	}
	
	public String getOrder()
	{
		return (Concert.toUpperCase()+","+Venue.toUpperCase()+","+SeatList.get(0)+","+SeatList.get(1)+","+ShowDate);
	}
	
}


class Account{
	
	private double balance ;
	
	Account(double initial_balance)
	{
		balance = initial_balance;
	}
	
	double getBalance()
	{
		return balance;
		
	}
	
	void setBalance(double amount)
	{
		balance = amount;
	}
	
}
