package org.moltimate.moltimatebackend.util;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HttpUtils {

    /**
     * Returns the page content at the given URL as a String.
     *
     * @param url to get page content from
     * @return Page content as a String
     * @throws IOException if URL is malformed or stream cannot be opened.
     */
    public static String readStringFromURL(String url) throws IOException {
        try (Scanner scanner = new Scanner(
                new URL(url).openStream(),
                StandardCharsets.UTF_8.toString()
        )
        ) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
