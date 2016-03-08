public class FavUser {
	int userID;
	String userName;

	public FavUser(int _userID, String _username) {
		userID = _userID;
		userName = _username;
	}

	public int getUserID() {
		return userID;
	}

	public String getUserName() {
		return userName;
	}
}
