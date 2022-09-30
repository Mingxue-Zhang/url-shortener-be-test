package com.github.vivyteam.url.api;

import com.github.vivyteam.url.api.contract.FullUrl;
import com.github.vivyteam.url.api.contract.ShortenedUrl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Random;

@RestController
public class UrlApi {
    private HashMap<String, String> full_short = new HashMap<String, String>();
    @GetMapping("/{url}/short")
    public Mono<ShortenedUrl> shortUrl(@PathVariable final String url) {
        // TODO: implement logic to shorten the url
        String getShortURL = "http://localhost:9000/"+ mapShortURL(url);
        //return getShortURL;
        return Mono.just(new ShortenedUrl(getShortURL));
    }

    @GetMapping("/{shortenedUrl}/full")
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Illegal arguments")
    public Mono<FullUrl> getFullUrl(@PathVariable final String shortenedUrl) {
        // TODO: implement logic to fetch the full url
        for (Object key: full_short.keySet()) {
            if (full_short.get(key).equals(shortenedUrl))
                return Mono.just(new FullUrl(key.toString()));
        }
        return Mono.error(new IllegalArgumentException());
    }

    @GetMapping("/shortenedUrl")
    public Mono<String> redirect(@PathVariable("shortenedUrl") String shortenedUrl) {
        return Mono.just("redirect:/"+ getFullUrl(shortenedUrl));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentHandler() {
        //
    }

    public String mapShortURL(String fullURL){
        if (full_short.containsKey(fullURL)) {
            return full_short.get(fullURL);
        }
        String shortURL = encodeURL(12);// Generate a 12-bit random string, or any length you want
        full_short.put(fullURL, shortURL);
        return shortURL;
    }
    public String encodeURL(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i = 0; i < length; i++ ) {
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
