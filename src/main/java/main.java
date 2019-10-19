import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import twitter.TwitterManager;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

public class main {
    public static void main(String[] args) {
        String searchTerm = "football";
        TwitterManager twitterManager = new TwitterManager();
        new File("twits").mkdir();
        String fileName = "twits/twits.json";
        try (FileWriter file = new FileWriter(fileName)) {
            List<Status> statusList;
            statusList = twitterManager.searchForTweets(searchTerm);
            file.write(twitterManager.getTweetsAsJson(statusList)
                    .toString());
            System.out.println("Successfully Copied JSON Object to File...");

            RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200));
            RestHighLevelClient client = new RestHighLevelClient(builder);

            statusList.forEach(status -> {
                IndexRequest indexRequest = new IndexRequest("tweets").source(twitterManager.getTweetAsMap(status));
                try {
                    IndexResponse response = client.index(indexRequest, DEFAULT);
                    System.out.println("INDEXING...");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
            client.close();
            System.out.println("END!");
        } catch (TwitterException | IOException e1) {
            e1.printStackTrace();
        }

    }
}

