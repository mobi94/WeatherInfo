package com.breezee.sergeystasyuk.weatherinfo;

import com.breezee.sergeystasyuk.weatherinfo.models.GeopositionSearchModelImpl;
import com.breezee.sergeystasyuk.weatherinfo.models.LocationsAPIInterface;
import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by User on 19.02.2017.
 */

public class GeopositionApiInterfaceTest {

    private static final String API_KEY = "M1WSwPglWwVHjqvdeFuIZwUkEMBlqolS";
    private static final String QUESTION = "49.9935000,36.2303830";
    private static final String LANGUAGE = "ru";

    private MockWebServer server;
    private LocationsAPIInterface apiInterface;

    private String readString(String fileName) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
        try {
            int size = stream.available();
            byte[] buffer = new byte[size];
            int result = stream.read(buffer);
            return new String(buffer, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

                if (request.getPath().equals("/search?apikey=" + API_KEY
                        + "&q=" + QUESTION + "&language=" + LANGUAGE)) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody(readString("geoposition.json"));
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);
        HttpUrl baseUrl = server.url("/");
        apiInterface = (new GeopositionSearchModelImpl(baseUrl.toString())).getApiInterface();
    }

    @Test
    public void testGetGeoposition() throws Exception {

        Map<String, String> request = new LinkedHashMap<>();
        request.put("apikey", API_KEY);
        request.put("q", QUESTION);
        request.put("language", LANGUAGE);

        TestSubscriber<GeopositionSearchResult> testSubscriber = new TestSubscriber<>();
        apiInterface.getLocationInfo(request).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);

        GeopositionSearchResult actual = testSubscriber.getOnNextEvents().get(0);

        assertEquals("323903", actual.getKey());
        assertEquals(2, actual.getTimeZone().getGmtOffset().intValue());
        assertEquals("Харьков", actual.getLocalizedName());
        assertEquals("Украина", actual.getCountry().getLocalizedName());
    }

    @Test
    public void testGetGetGeopositionIncorrect() throws Exception {
        try {
            apiInterface.getLocationInfo(new LinkedHashMap<>()).subscribe();
            fail();
        } catch (Exception expected) {
            assertEquals("HTTP 404 Client Error", expected.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}
