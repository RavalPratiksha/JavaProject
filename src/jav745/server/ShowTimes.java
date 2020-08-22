package jav745.server;
import java.util.LinkedList;
import java.util.List;


public class ShowTimes {
	private String Concert;
	private String Venue;
	List<Integer> SeatList = new LinkedList<>();
	List<Double> PriceList = new LinkedList<>();
	private String ShowDate;

	public ShowTimes(String C, String V, List<Integer> s, List<Double> p, String d) {
		Concert = C;
		Venue = V;
		SeatList = s;
		PriceList = p;
		
		ShowDate = d;

	}

	String getConcert() {
		return Concert;
	}

	String getVenue() {
		return Venue;
	}

	List<Integer> getSeatList() {
		return SeatList;
	}

	List<Double> getPriceList() {
		return PriceList;
	}

	String getShowDate() {
		return ShowDate;
	}

	void setNewSeats(int index, int value) {
		SeatList.set(index, value);

	}

}