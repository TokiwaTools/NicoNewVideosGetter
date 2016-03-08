import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class NicoConfig {
	
	String address = "";
	String password = "";
	String path = "";
	
	public NicoConfig(String _path) {
		path = _path;
	}
	
	public String getAddress() {
		try {
			loadJSON();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	public String getPassword() {
		try {
			loadJSON();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return password;
	}
	
	public void loadJSON() throws IOException {
		try {
			JsonReader jsonReader = new JsonReader( new FileReader(path));
			jsonReader.beginObject();

			while (jsonReader.hasNext()) {
				String name = jsonReader.nextName();
				if (name.equals("address")) {
					address = jsonReader.nextString();
				}
				if (name.equals("password")) {
					password = jsonReader.nextString();
				}
			}

			jsonReader.endObject();
			jsonReader.close();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
	}

	public void createJSON(String _address, String _password) {
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		MyAccount account = new MyAccount(_address, _password);
		PrintWriter json;
		try {
			json = new PrintWriter(file);
			json.print(gson.toJson(account));
			json.flush();
			json.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
