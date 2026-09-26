package etu.ecole.fluxrssmobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssSaxHandler extends DefaultHandler {
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

    private final RssFeedViewModel viewModel;

    public RssSaxHandler(RssFeedViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setUrl(String url){
        this.url= url;
    }

    public void processFeed(){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(this);
            InputStream inputStream = new URL(url).openStream();
            reader.parse(new InputSource(inputStream));
        } catch (Exception e) {
            Log.e("RSS", "processFeed exception" + e);
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
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

            case "media:content":
                imageURL = attributes.getValue("url");
                image = getBitmap(imageURL);
        }
    }

    public void characters(char ch[], int start, int length) {
        String chars = new String(ch).substring(start, start+length);

        if (!inItem) return;

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
        if (qName.equals("item")) {
            inItem = false;
            ItemModel newItem = new ItemModel(title.toString(), description.toString(), date.toString(), image);
            viewModel.addItem(newItem);
        }
    }

    // récupération d'une image sous format Bitmap à partir d'une url
    public Bitmap getBitmap(String imageURL) {
        try {
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }

    }


}
