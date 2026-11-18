package dev.danvega.tracker.carrier;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class CarrierErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            String path = url.getPath();
            String trackingNumber = path.substring(path.lastIndexOf('/') + 1);
            throw new ShipmentNotFoundException(trackingNumber);
        }
        super.handleError(url, method, response);
    }

}
