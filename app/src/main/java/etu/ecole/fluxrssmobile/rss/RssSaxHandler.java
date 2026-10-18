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

    // Indique si le parseur se trouve dans un <item>
    private boolean inItem = false ;
    // Texte de la balise en cours de lecture
    private StringBuilder currentText = null ;

    private String title = "" ;
    private String description = "" ;
    private String date = "" ;
    // L'image référencée par l'attribut url du tag <media:content>
    private Bitmap image = null ;

    private final RssFeedViewModel viewModel;

    public RssSaxHandler(RssFeedViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setUrl(String url){
        this.url= url;
    }

    public void parse(InputStream in) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setContentHandler(this);
        reader.parse(new InputSource(in));
    }

    public void processFeed(){
        try {
            InputStream inputStream = new URL(url).openStream();
            parse(inputStream);
        } catch (Exception e) {
            Log.e("RSS", "processFeed exception" + e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("item")) {
            // Nouvel article
            inItem = true;
            title = "";
            description = "";
            date = "";
            image = null;
            return;
        }
        if (!inItem) return;

        switch (qName) {
            case "title":
            case "description":
            case "pubDate":
                currentText = new StringBuilder();
                break;

            case "media:content":
                String imageURL = attributes.getValue("url");
                image = BitmapUtility.getBitmapFromUrl(imageURL);
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // on accumule les morceaux car SAX peut découper les textes
        if (currentText != null) {
            currentText.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("item")) {
            inItem = false;
            viewModel.addItem(new RssItemModel(title, description, date, image));
            return;
        }
        if (currentText == null) return;


        String text = currentText.toString().trim();
        switch (qName) {
            case "title":
                title = text;
                break;
            case "description":
                description = text;
                break;
            case "pubDate":
                date = text;
                break;
        }
        currentText = null;
    }
}
