package etu.ecole.fluxrssmobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MyRSSsaxHandler extends DefaultHandler {
    private String url = null ;// l'URL du flux RSS à parser
    // Ensemble de drapeau permettant de savoir où en est le parseur dans le flux XML
    private boolean inTitle = false ;
    private boolean inDescription = false ;
    private boolean inItem = false ;
    private boolean inDate = false ;
    // L'image référencée par l'attribut url du tag <enclosure>
    private Bitmap image = null ;
    private String imageURL = null ;
    // Le titre, la description et la date extraits du flux RSS
    private StringBuffer title = new StringBuffer();
    private StringBuffer description = new StringBuffer();
    private StringBuffer date = new StringBuffer();
    private int numItem = 0; // Le numéro de l'item à extraire du flux RSS
    private int numItemMax = - 1; // Le nombre total d’items dans le flux RSS

    private RSSFeedViewModel rssFeedViewModel;

    public MyRSSsaxHandler(RSSFeedViewModel rssFeedViewModel) {
        this.rssFeedViewModel = rssFeedViewModel;
    }

    public void setUrl(String url){
        this.url= url;
    }

    public void processFeed(){
        try {
            numItem = 0; //A modifier pour visualiser un autre item
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(this);
            InputStream inputStream = new URL(url).openStream();
            reader.parse(new InputSource(inputStream));
            image = getBitmap(imageURL);
            numItemMax = numItem;
        } catch (Exception e) {
            Log.e("smb116rssview", "processFeed Exception : " + e.getMessage());
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // qname = le nom de la balise
        if (qName.equals("item")) {
            inItem = true;
            return;
        }
        switch (qName) {
            case "title":
                inTitle = true;
                break;

            case "description":
                inDescription = true;
                break;

            case "pubDate":
                inDate = true;
                break;
        }
    }

    public void characters(char ch[], int start, int length) {
        String chars = new String(ch).substring(start, start+length);

        if (!inItem) return;
        //Log.d("chars", chars);
        if (inTitle) {
            title = new StringBuffer();
            title.append(chars);
            inTitle = false;
        }
        if (inDescription) {
            description = new StringBuffer();
            description.append(chars);
            inDescription = false;
        }
        if (inDate) {
            date = new StringBuffer();
            date.append(chars);
            inDate = false;
        }

    }

    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "item":
                inItem = false;
                ItemModel newItem = new ItemModel(title.toString(), description.toString(), date.toString());
                rssFeedViewModel.addItem(newItem);
                numItem++;
                break;

            case "rss":
                Log.d("items", rssFeedViewModel.getItems().toString());
                break;
        }
    }

    // récupération d'une image sous format Bitmap à partir d'une url
    public Bitmap getBitmap(String imageURL) {
        try {
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //  connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }

    }


}
