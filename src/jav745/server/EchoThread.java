package jav745.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Formatter;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class EchoThread extends Thread {
	private Socket socket = null;
	ShowTimes[] serverData;
	int flag;

	/**
	 * Constructor takes socket obtained from successful connection to a
	 * serversocket class.
	 * 
	 * connectivity to a particular client is handled by this thread so the server
	 * can listen for and handle other client connection requests
	 * 
	 * @param socket
	 */
	public EchoThread(Socket socket, ShowTimes[] s2,int f) {
		super("EchoThread");
		this.socket = socket;
		this.serverData = s2;
		this.flag=f;

	}

	/**
	 * This code runs in a thread separate from the main one.
	 */
	
	public void run() {
		String File = "Transaction" + flag+".txt";
		try (
				// open streams for reading and writing to the
				// client
				
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				Formatter f = new Formatter(File);) {
		
			String Client_Number = "CLIENT00" + flag;
			Double Server_Balance = 0.0;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			System.out.println("New Ticket Request received from client..");
			
			
			while (true) {
				
				Scanner data = new Scanner(in.readLine());
				data.useDelimiter(",");
				socket.setSoTimeout(0);
				Double Payment = 0.0;
				
				while (data.hasNext()) {

				
					String Concert = data.next();
					String Venue = data.next();
					int Seat_1 = data.nextInt();
					int Seat_2 = data.nextInt();
					String Date = data.next();

					System.out.println(Client_Number + "," + Concert + "," + Venue + "," + Seat_1 + " Gold Seats , "
							+ Seat_2 + " Silver Seats , " + Date);

					for (int i = 0; i < serverData.length; i++) {

						if (serverData[i].getConcert().toUpperCase().equals(Concert) && serverData[i].getShowDate().equals(Date)) {
							if (serverData[i].SeatList.get(0) != 0 || serverData[i].SeatList.get(1) != 0) {
								System.out.println("Available Tickets :");
								System.out.println(serverData[i].getSeatList().get(0) + " Gold Seats");
								System.out.println(serverData[i].getSeatList().get(1) + " Silver Seats");
								System.out.println("Ready to Pay ?\n");
								out.println("YES");
								break;
							} else
								{System.out.println("Tickets are not Available");
								f.format("\n"+Client_Number+ LocalDateTime.now().format(formatter) +" Tickets are not Available");
								break;}
						}
					}

					if (in.readLine().equalsIgnoreCase("YES")) {
						System.out.println("Received Confirmation from client.\n");
						for (int i = 0; i < serverData.length; i++) {
							if (serverData[i].getConcert().toUpperCase().equals(Concert) && serverData[i].getShowDate().equals(Date)
									&& (serverData[i].SeatList.get(0) != 0 || serverData[i].SeatList.get(1) != 0)) {
								int Gold_Tickets = serverData[i].getSeatList().get(0) - Seat_1;
								int Silver_Ticket = serverData[i].getSeatList().get(1) - Seat_2;
								Payment = (Seat_1 * serverData[i].getPriceList().get(0))
										+ (Seat_2 * serverData[i].getPriceList().get(1));
								System.out.printf("%d Gold Tickets and %d Silver Tickets Reserved.\n", Seat_1,
										Seat_2);
								System.out.println(
										"\nWaiting for Payment of " + Payment + " From Cleint " + Client_Number);
								
								out.println(Payment);
								serverData[i].setNewSeats(0, Gold_Tickets);
								serverData[i].setNewSeats(1, Silver_Ticket);
								break;
							}
						}

					}
					
								
					try {
						socket.setSoTimeout(60 * 1000);
						System.out.println("Waiting for Client Confirmation");
					} catch (Exception e) {
						System.err.println("Session Timeout");
						break;
					}
					// throw new SessionTimeoutException();

					if (in.readLine().equalsIgnoreCase("YES")) {
						System.out.printf("Received Payment of %.2f from Client %s", Payment, Client_Number);
						Server_Balance += Payment;
						System.out.println("\nYour Tickets Are Confirmed!!!\n");
						f.format("\n"+Client_Number +" "+ LocalDateTime.now().format(formatter)+" Tickets Confirmed :" + Seat_1 +" Gold Tickets " +Seat_2+" Silver Tickets");

					} else {
						System.out.println("Order Cancelled!!!\n");
						f.format("\n"+Client_Number+" "+LocalDateTime.now().format(formatter) +" Order Cancelled by Client");
					}
				}
				
			data.close();	
			System.out.println("Processes All Requests");
			
		}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		catch (NullPointerException e) {
			System.err.println("Client Request Completed.");
			} 
	}
}