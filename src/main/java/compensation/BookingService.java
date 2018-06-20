package compensation;

public class BookingService {

	public boolean book(BookingRequest b) {
		System.out.println("Booking");
		return true;
	}

	public boolean cancelBooking(BookingRequest b) {
		System.out.println("Cancelling");
		return true;
	}

	public boolean debitCard(BookingRequest b) {
		System.out.println("Debit card");
		return true;
	}

	public boolean updateCustomer(BookingRequest b) {
		System.out.println("Update customer");
		return true;
	}
}
