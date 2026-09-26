package etu.ecole.fluxrssmobile.rss;

import android.graphics.Bitmap;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import etu.ecole.fluxrssmobile.BitmapUtility;

public class RssSaxHandler extends DefaultHandler {
    private String url = null ;// l'URL du flux RSS à parser

    // Ensemble de drapeau permettant de savoir où en est le parseur dans le flux XML
    private boolean inTitle = false ;
    private boolean inDescription = false ;
    private boolean inItem = false ;
    private boolean inDate = false ;
    // L'image référencée par l'attribut url du tag <enclosure>
    private Bitmap image = null ;

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
                String imageURL = attributes.getValue("url");
                image = BitmapUtility.getBitmapFromUrl(imageURL);
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
            RssItemModel newItem = new RssItemModel(title.toString(), description.toString(), date.toString(), image);
            viewModel.addItem(newItem);
        }
    }


}
