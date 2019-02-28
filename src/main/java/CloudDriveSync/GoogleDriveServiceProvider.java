package CloudDriveSync;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GoogleDriveServiceProvider {

    private static String CLIENT_ID = "1050663558595-0o6pu82hde469u1ru70d8441otcjgq4t.apps.googleusercontent.com";
    private static String CLIENT_SECRET = "JUdMCKIBHBgH08Wq6UoOr1ea";

    private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private static GoogleDriveServiceProvider INSTANCE;
    private Drive googleDriveClient;

    private GoogleDriveServiceProvider() {
        try {
            initGoogleDriveServices();
        } catch (IOException e) {
            System.out.println("Failed to create the Google drive client. Please check your Internet connection and your Google credentials.");
            e.printStackTrace();
        }
    }

    public static GoogleDriveServiceProvider get() {
        if (INSTANCE == null) {
            INSTANCE = new GoogleDriveServiceProvider();
        }
        return INSTANCE;
    }

    public void initGoogleDriveServices() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();

        String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        System.out.println("Please open the following URL in your browser then type the authorization code:");
        System.out.println("  " + url);
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter Code Here: ");
        String code = br.readLine();

        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

        //Create a new authorized API client
        googleDriveClient = new Drive.Builder(httpTransport, jsonFactory, credential).build();
    }

    public Drive getGoogleDriveClient() {
        return googleDriveClient;
    }
}
