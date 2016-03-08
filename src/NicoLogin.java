import java.io.IOException;
import java.util.ArrayList;

import javax.security.auth.login.FailedLoginException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class NicoLogin {
	String address = "";
	String password = "";
	
	public NicoLogin(String _address, String _password) {
		address = _address;
		password = _password;
	}
	
	public CookieStore getUserSession() throws ClientProtocolException, IOException, FailedLoginException {
		final String url = "https://secure.nicovideo.jp/secure/login?site=niconico";

		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);
		client.getParams().setParameter("http.connection.timeout", 5000);
		client.getParams().setParameter("http.socket.timeout", 3000);

		HttpPost post = new HttpPost(url);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("mail", address));
		params.add(new BasicNameValuePair("password", password));

		post.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse response = client.execute(post);
		
		boolean b = false;
		Header[] headers = response.getAllHeaders();
		for (Header h : headers) {
			if (h.getName().equals("Set-Cookie")) {
				if (h.getValue().lastIndexOf("deleted") != -1) {
					b = true;
				}
			}
			System.out.println(h.getName() + ":" + h.getValue());
		}
		if (b == false) {
			throw new FailedLoginException();
		}

		//Keep Cookie
		CookieStore _cookieStore = client.getCookieStore();
		client.getConnectionManager().shutdown();
		return _cookieStore;
	}

}
