import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.stream.JsonReader;

public class NewVideosOPML {

	NicoLogin nicoLogin;
	String path = "";		//Saved Path
	int rssType = 0;		//RSS 2.0: 0 | Atom: 1

	public NewVideosOPML() {
	}
	
	public boolean save(String _path, CookieStore _cookieStore) {
		File jsonFile = new File("src\\data\\watchlist.json");
		try {
			jsonFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pWriter;
		
		FavUser [] favUsers = null;
		try {
			pWriter = new PrintWriter(
					new BufferedWriter(
							new FileWriter(jsonFile)));
			try {
				String json = getWatchList(_cookieStore);
				pWriter.println(json);
				pWriter.close();
			} catch (Exception e) {
				System.out.println("Watchlist Access Error");
			}

			try {
				favUsers = getFavUsers("src\\data\\watchlist.json");
			} catch (IOException e) {
				System.out.println("Watchlist Loading Error");
			}
		} catch (IOException e) {
			System.out.println("File Creation Error");
			return false;
		}
		
		try {
			writeDocument(new File(_path), OPMLDocument(favUsers, rssType));
			System.out.println("Created OPML\n" + _path);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to create OPML");
			return false;
		}
		return true;
	}
	
	private String getWatchList(CookieStore _cookieStore) {
		try {
			HttpGet method = new HttpGet("http://www.nicovideo.jp/api/watchitem/list");

			DefaultHttpClient client = new DefaultHttpClient();
			client.setCookieStore(_cookieStore);
			method.setHeader("Connection", "Keep-Alive");
			HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_OK) {
				throw new Exception("");
			}

			String json = EntityUtils.toString(response.getEntity(), "UTF-8");
			return json;
		} catch (Exception e) {
			return null;
		}
	}

	private FavUser [] getFavUsers(String _file) throws IOException {
		JsonReader jsonReader = new JsonReader(
				new BufferedReader(
						new FileReader(_file)));

		int total = 0;
		jsonReader.beginObject();

		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			if (name.equals("total_count")) {
				total =  jsonReader.nextInt();
			} else {
				jsonReader.skipValue();
			}
		}

		jsonReader.endObject();
		jsonReader.close();

		jsonReader = new JsonReader(
				new BufferedReader(
						new FileReader(_file)));

		FavUser [] users = new FavUser[total];

		jsonReader.beginObject();

		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			if (name.equals("watchitem")) {
				jsonReader.beginArray();

				for (int i=0; jsonReader.hasNext(); i++) {
					int userID = 0;
					String userName = "";

					jsonReader.beginObject();

					for (int j = 0; jsonReader.hasNext(); j++) {
						String itemName = jsonReader.nextName();
						if (itemName.equals("item_data")) {
							jsonReader.beginObject();

							while (jsonReader.hasNext()) {
								String itemDataName = jsonReader.nextName();
								if (itemDataName.equals("id")) {
									userID = jsonReader.nextInt();
								} else if (itemDataName.equals("nickname")) {
									userName = jsonReader.nextString();
								} else {
									jsonReader.skipValue();
								}
							}

							jsonReader.endObject();
						} else {
							jsonReader.skipValue();
						}
					}

					jsonReader.endObject();

					if (userID != 0) {
						users[i] = new FavUser(userID, userName);
					}
				}

				jsonReader.endArray();
			} else {
				jsonReader.skipValue();
			}
		}

		jsonReader.endObject();

		jsonReader.close();

		return users;
	}

	private Document OPMLDocument(FavUser [] _users, int _type) throws ParserConfigurationException {
		if (_type != 0 && _type != 1) {
			throw new IllegalArgumentException();
		}
		
		DocumentBuilder documentBuilder = DocumentBuilderFactory
								.newInstance()
								.newDocumentBuilder();
		
		Document document = documentBuilder.newDocument();
		
		Element eOpml = document.createElement("opml");
		eOpml.setAttribute("version", "1.0");
		Element eHead = document.createElement("head");
		Element eTitle = document.createElement("title");
		eTitle.setTextContent("Nicovideos");
		eHead.appendChild(eTitle);
		eOpml.appendChild(eHead);
		Element eBody = document.createElement("body");
		Element eOutline = document.createElement("outline");
		eOutline.setAttribute("text", "Nicovideos");
		eOutline.setAttribute("title", "Nicovideos");
		
		for (int i = 0; i < _users.length; i++) {
			Element eUser = document.createElement("outline");
			
			String url = "http://www.nicovideo.jp/user/" + _users[i].getUserID() + "/video";
			String xmlUrl = "";
			if (_type == 0) {
				xmlUrl = url + "?rss=2.0";
				eUser.setAttribute("type", "rss");
			} else if (_type == 1) {
				xmlUrl = url + "?rss=atom";
				eUser.setAttribute("type", "atom");
			}
			eUser.setAttribute("text", _users[i].getUserName() + "‚Ì“Še“®‰æ");
			eUser.setAttribute("title", _users[i].getUserName() + "‚Ì“Še“®‰æ");
			eUser.setAttribute("xmlUrl", xmlUrl);
			eUser.setAttribute("htmlUrl", url);
			eOutline.appendChild(eUser);
		}
		
		eBody.appendChild(eOutline);
		eOpml.appendChild(eBody);
		document.appendChild(eOpml);
		return document;
	}
	
	private boolean writeDocument(File _file, Document _document) {
		Transformer transformer = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory
														.newInstance();
			transformer = transformerFactory.newTransformer();														
		} catch (TransformerException e) {
			e.printStackTrace();
			return false;
		}
		
		transformer.setOutputProperty("indent", "yes");
		transformer.setOutputProperty("encoding", "UTF-8");
		
		try {
			transformer.transform(new DOMSource(_document), new StreamResult(_file));
		} catch (TransformerException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
