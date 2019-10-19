package twitter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitterManager {

    public List<Status> searchForTweets(String searchParameter) throws TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query(searchParameter);
        query.setCount(10000);
        QueryResult queryResult = twitter.search(query);
        List<Status> statusList = new ArrayList<>();
        while (queryResult.hasNext() && queryResult.getRateLimitStatus().getRemaining() > 5) {
            statusList.addAll(queryResult.getTweets());
            queryResult = twitter.search(queryResult.nextQuery());
        }
        return statusList;
    }

    public JsonArray getTweetsAsJson(List<Status> statusList) {
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();
        statusList.forEach(status -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("created_at", gson.toJsonTree(status.getCreatedAt()));
            jsonObject.add("full_text", gson.toJsonTree(status.getText()));
            jsonObject.add("user", gson.toJsonTree(status.getUser().getName()));
            jsonArray.add(jsonObject);
        });
        return jsonArray;
    }

    public Map<String, String> getTweetAsMap(Status status) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("created_at", status.getCreatedAt().toString());
        dataMap.put("full_text", status.getText());
        dataMap.put("user", status.getUser().getName());
        return dataMap;
    }

}
