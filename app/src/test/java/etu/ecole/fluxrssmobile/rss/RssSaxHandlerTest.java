package etu.ecole.fluxrssmobile.rss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RssSaxHandlerTest {
    // Nécessaire car RssFeedViewModel utilise LiveData
    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private List<RssItemModel> parse(String xml) throws Exception {
        RssFeedViewModel vm = new RssFeedViewModel();
        new RssSaxHandler(vm).parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        return vm.getItems();
    }

    @Test
    public void simpleItem_isParsed() throws Exception {
        List<RssItemModel> items = parse(
                "<rss><channel><title>Le Monde</title>"
                        + "<item><title>Mon titre</title><description>Ma description</description><pubDate>Lundi</pubDate></item>"
                        + "</channel></rss>");

        assertEquals(1, items.size());
        assertEquals("Mon titre", items.get(0).getTitle());
        assertEquals("Ma description", items.get(0).getDescription());
        assertEquals("Lundi", items.get(0).getDate());
    }

    @Test
    public void channelTitle_isNotAnItem() throws Exception {
        List<RssItemModel> items = parse("<rss><channel><title>Le Monde</title></channel></rss>");
        assertTrue(items.isEmpty());
    }

    @Test
    public void cdataTitle_isTrimmed() throws Exception {
        List<RssItemModel> items = parse(
                "<rss><channel><item>"
                        + "<title><![CDATA[ Canada : un incendie sur une base militaire ]]></title>"
                        + "</item></channel></rss>");
        assertEquals("Canada : un incendie sur une base militaire", items.get(0).getTitle());
    }


    @Test
    public void titleWithEntity_isNotTruncated() throws Exception {
        List<RssItemModel> items = parse(
                "<rss><channel><item><title>Paiements &amp; banques</title></item></channel></rss>");
        assertEquals("Paiements & banques", items.get(0).getTitle());
    }

    @Test
    public void numericCharacterReference_isNotTruncated() throws Exception {
        List<RssItemModel> items = parse(
                "<rss><channel><item><title>L&#8217;économie</title></item></channel></rss>");
        assertEquals("L\u2019économie", items.get(0).getTitle());
    }

    @Test
    public void missingField_doesNotLeakFromPreviousItem() throws Exception {
        List<RssItemModel> items = parse(
                "<rss><channel>"
                        + "<item><title>A</title><description>Description of A</description></item>"
                        + "<item><title>B</title></item>"
                        + "</channel></rss>");
        assertEquals("", items.get(1).getDescription());
    }

    @Test
    public void textSplitAcrossSeveralCalls_isAccumulated() {
        RssFeedViewModel viewModel = new RssFeedViewModel();
        RssSaxHandler handler = new RssSaxHandler(viewModel);
        char[] first = "Article sur les ".toCharArray();
        char[] second = "paiements".toCharArray();

        handler.startElement("", "", "item", null);
        handler.startElement("", "", "title", null);
        handler.characters(first, 0, first.length);
        handler.characters(second, 0, second.length);
        handler.endElement("", "", "title");
        handler.endElement("", "", "item");

        assertEquals("Article sur les paiements", viewModel.getItems().get(0).getTitle());
    }
}
