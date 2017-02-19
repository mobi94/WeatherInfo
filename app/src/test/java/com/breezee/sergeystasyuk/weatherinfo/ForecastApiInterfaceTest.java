package com.breezee.sergeystasyuk.weatherinfo;

import com.breezee.sergeystasyuk.weatherinfo.models.ForecastAPIInterface;
import com.breezee.sergeystasyuk.weatherinfo.models.ForecastModelImpl;
import com.breezee.sergeystasyuk.weatherinfo.pojos.dailyforecast.DailyForecastResult;
import com.breezee.sergeystasyuk.weatherinfo.pojos.fivedaysforecast.FiveDaysForecastResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by User on 18.02.2017.
 */

public class ForecastApiInterfaceTest {

    private static final String LOCATION_KEY = "323903";
    private static final String API_KEY = "M1WSwPglWwVHjqvdeFuIZwUkEMBlqolS";
    private static final String LANGUAGE = "ru";
    private static final String DETAILS = "true";
    private static final String METRIC = "true";

    private MockWebServer server;
    private ForecastAPIInterface apiInterface;

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

                if (request.getPath().equals("/currentconditions/v1/" + LOCATION_KEY + "?apikey=" + API_KEY
                        + "&language=" + LANGUAGE + "&details=" + DETAILS)) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody(readString("current_forecast.json"));
                } else if (request.getPath().equals("/forecasts/v1/daily/5day/" + LOCATION_KEY + "?apikey=" + API_KEY
                        + "&language=" + LANGUAGE + "&metric=" + METRIC)) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody(readString("five_days_forecast.json"));
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);
        HttpUrl baseUrl = server.url("/");
        apiInterface = (new ForecastModelImpl(baseUrl.toString())).getApiInterface();
    }

    @Test
    public void testGetCurrentForecast() throws Exception {

        Map<String, String> request = new LinkedHashMap<>();
        request.put("apikey", API_KEY);
        request.put("language", LANGUAGE);
        request.put("details", DETAILS);

        TestSubscriber<List<DailyForecastResult>> testSubscriber = new TestSubscriber<>();
        apiInterface.getDailyForecast(LOCATION_KEY, request).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);

        List<DailyForecastResult> actual = testSubscriber.getOnNextEvents().get(0);

        assertEquals(1, actual.size());
        assertEquals(1487453100, actual.get(0).getEpochTime().longValue());
        assertEquals("Облачно", actual.get(0).getWeatherText());
        assertEquals(false, actual.get(0).getIsDayTime());
    }

    @Test
    public void testGetCurrentForecastIncorrect() throws Exception {
        try {
            apiInterface.getDailyForecast("IncorrectRequest", new LinkedHashMap<>()).subscribe();
            fail();
        } catch (Exception expected) {
            assertEquals("HTTP 404 Client Error", expected.getMessage());
        }
    }

    @Test
    public void testGetFiveDaysForecast() throws Exception {

        Map<String, String> request = new LinkedHashMap<>();
        request.put("apikey", API_KEY);
        request.put("language", LANGUAGE);
        request.put("metric", METRIC);

        TestSubscriber<FiveDaysForecastResult> testSubscriber = new TestSubscriber<>();
        apiInterface.getFiveDaysForecast(LOCATION_KEY, request).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);

        FiveDaysForecastResult actual = testSubscriber.getOnNextEvents().get(0);

        assertEquals(5, actual.getDailyForecasts().size());
        assertEquals(1487610000, actual.getHeadline().getEffectiveEpochDate().longValue());
        assertEquals("Гололед В понедельник вечером", actual.getHeadline().getText());
        assertEquals("Облачно", actual.getDailyForecasts().get(0).getDay().getIconPhrase());
    }

    @Test
    public void testGetFiveDaysForecastIncorrect() throws Exception {
        try {
            apiInterface.getFiveDaysForecast("IncorrectRequest", new LinkedHashMap<>()).subscribe();
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
