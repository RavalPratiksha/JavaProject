package jav745.server;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class jav745p2server {
	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.err.println("Usage: java jav745p2server <port number> <input file> <starting Balance> ");
			System.exit(1);
		}
		int portNumber = Integer.parseInt(args[0]);
		Path path = Paths.get(args[1]);
		
		ShowTimes[] S ;
		
		S = LoadServerData(path);
		int flag=1;
		
		try ( ServerSocket serverSocket =
				new ServerSocket(Integer.parseInt(args[0]), 100);
	        ) {
	
	        while (true) {
	        	
	        	new EchoThread(serverSocket.accept(),S,flag++).start();
	        	
	        	System.out.println("On to the next request");
	        }
        } catch (IOException e) {
        	System.err.println("Could not listen on port " + portNumber);
        	e.printStackTrace();
        	System.exit(-1);
	    }
	}

	private static ShowTimes[] LoadServerData(Path path) {
		
		ShowTimes[] S = new ShowTimes[2000];
		int count = 0;
		
		try (Scanner s = new Scanner(path)) {

			while (s.hasNextLine()) {
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

					List<Double> PriceList = new LinkedList<>();
					PriceList.add(data.nextDouble());
					PriceList.add(data.nextDouble());
					String ShowDate = data.next();

					S[count] = new ShowTimes(Concert, Venue, SeatList, PriceList, ShowDate);
					count++;
					

				}

			}

		}
		catch (Exception e) {
			System.out.println("Error");
		}
		return S;
	}
}
	