/**
 * Sample Java code for youtube.liveStreams.list
 * See instructions for running these code samples locally:
 * https://developers.google.com/explorer-help/code-samples#java
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

public class EriTwitchBot{
    private static final String CLIENT_SECRETS= "C:\\Users\\Eribyte\\IdeaProjects\\EriTwitchVTS\\src\\main\\resources\\client_secrets.json";
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");

    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final Logger log = LogManager.getLogger();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in =  Files.newInputStream(Paths.get(CLIENT_SECRETS));
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static int compareLiveBroadcast(LiveBroadcast a, LiveBroadcast b){
        long unixTime = System.currentTimeMillis() / 1000L;

        long d1 = Math.abs(a.getSnippet().getScheduledStartTime().getValue() - unixTime);
        long d2 = Math.abs(b.getSnippet().getScheduledStartTime().getValue() - unixTime);
        return Long.compare(d1,d2);
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static void main(String[] args)
            throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        List<String> x = new ArrayList<>();
        x.add("snippet");

        List<String> y = new ArrayList<>();
        y.add("authorDetails");

        YouTube.LiveBroadcasts.List request = youtubeService.liveBroadcasts()
                .list(x);
        LiveBroadcastListResponse response = request.setBroadcastStatus("active").execute();

        Date now = new Date();

        log.info(now.getTime());

        List<LiveBroadcast> liveId = response.getItems().stream().filter((s1)->s1.getSnippet().getScheduledStartTime().getValue() > now.getTime()).sorted(EriTwitchBot::compareLiveBroadcast).collect(Collectors.toList());//.getId();

        for(LiveBroadcast lbct : liveId){
            log.info(lbct.getSnippet().getTitle());
        }

        log.info(liveId);

//        YouTube.LiveChatMessages.List request2 = youtubeService.liveChatMessages()
//                .list(liveId, y);
//
//        LiveChatMessageListResponse response2 = request2.execute();
//
//        System.out.println(response2.getItems());
//
//        ;
    }
}