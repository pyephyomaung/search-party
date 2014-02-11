package com.BrotherOfLewis.SearchPartyPocket.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.BrotherOfLewis.SearchPartyPocket.Models.QueryQuestion;
import com.BrotherOfLewis.SearchPartyPocket.Models.Suggestion;
import com.BrotherOfLewis.SearchPartyPocket.SearchPartyPocketApp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GoogleSuggestionHelper extends AsyncTask<String, Void, List<QueryQuestion>>  {
    private static final String TAG = GoogleSuggestionHelper.class.getSimpleName();

    public GoogleSuggestionHelper()
    {
    }

    @Override
    protected List<QueryQuestion> doInBackground(String... queries) {
        boolean hasError = false;
        String errorMsg = "";
        List<QueryQuestion> rtn = new ArrayList<QueryQuestion>();
        for (String query : queries)
        {
            Suggestion[] suggestions = new Suggestion[0];
            try {
                suggestions = GetTheArrayOfSuggestions(query);
                if (suggestions  == null) Log.v(TAG, "sugguestions are null");
                QueryQuestion tmp = new QueryQuestion(query, suggestions);
                rtn.add(tmp);
            } catch (IOException e) {
                hasError = true;
                errorMsg = "No connection";
                break;
            } catch (ParserConfigurationException e) {
                hasError = true;
                errorMsg = e.getMessage();
                break;
            } catch (SAXException e) {
                hasError = true;
                errorMsg = e.getMessage();
                break;
            } catch (Exception e) {
                hasError = true;
                errorMsg = e.getMessage();
                break;
            }
        }
        return rtn;
    }

    private static Suggestion[] GetTheArrayOfSuggestions(String theSearch) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        Log.v(TAG, "GetTheArrayOfSuggestions theSearch = " + theSearch);
        ArrayList<Suggestion> resultList = new ArrayList<Suggestion>();

        String searchQuery = Constants.GOOGLE_URL.replace(Constants.REPLACE_STRING, URLEncoder.encode(theSearch, "utf8"));
        StringBuilder sb = new StringBuilder(searchQuery);

        HttpClient httpclient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet(sb.toString());

        HttpResponse response;

        response = httpclient.execute(request);
        HttpEntity r_entity = response.getEntity();
        String xmlString = EntityUtils.toString(r_entity);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = factory.newDocumentBuilder();
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(xmlString));
        Document doc = db.parse(inStream);

        NodeList completeSuggestionNL = doc.getElementsByTagName(Constants.COMPLETE_SUGGESTION);
        Log.v(TAG, "search = " + theSearch + ", completeSuggestionNL.getLength() = " + completeSuggestionNL.getLength());
        for(int i = 0; i < completeSuggestionNL.getLength(); i++)
        {
            Node node = completeSuggestionNL.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                Node suggestionNode = element.getElementsByTagName(Constants.SUGGEST_STRING).item(0);
//                    Node queriesNumNode = element.getElementsByTagName(Constants.QUERIES_STRING).item(0);

                Element suggestionElement = (Element) suggestionNode;
//                    Element queriesNumElement = (Element) queriesNumNode;

                String suggestionData = suggestionElement.getAttribute(Constants.SUGGEST_DATA);
//                    int queriesNumInt = Integer.parseInt(queriesNumElement.getAttribute(Constants.QUERIES_INT));

                resultList.add(new Suggestion(suggestionData, 0));
            }
        }
        r_entity.consumeContent();

        return resultList.toArray(new Suggestion[resultList.size()]);
    }


    public static boolean isNetworkAvailable() {
        String query = "seafood";
        Suggestion[] suggestions = new Suggestion[0];
        try {
            suggestions = GetTheArrayOfSuggestions(query);
            if (suggestions  == null) throw new Exception("suggestion is null");
        } catch (Exception e) {
           Log.v(TAG, "seafood: " + e.getMessage());
           return false;
        }
        return true;
    }
}
