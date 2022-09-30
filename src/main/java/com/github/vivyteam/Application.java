package com.github.vivyteam;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Random;

@SpringBootApplication
public class Application {

    private HashMap<String, String> full_short = new HashMap<String, String>(); // key: full URL; value: shorted url

    @GetMapping("/{urlToBeShortened}/short")
    @ResponseBody
    public String toShortURL(@PathVariable("urlToBeShortened") String urlToBeShortened) { // shorten a URL
        String getShortURL = full_short.getOrDefault(urlToBeShortened, mapShortURL(urlToBeShortened));
        return "http://localhost:9000/" + getShortURL;
    }

    @GetMapping("/{shortenedUrl}/full") //short-to-full
    @ResponseBody
    public String getFullURL(@PathVariable("shortenedUrl") String shortenedUrl) {
        if (full_short.containsValue(shortenedUrl)) {
            for (Object key: full_short.keySet()) {
                if (full_short.get(key).equals(shortenedUrl))
                    return key.toString();
            }
        }
        return "Missing URL";
    }

    // redirect to full URL
    @GetMapping("/shortenedUrl")
    public String redirect(@PathVariable("shortenedUrl") String shortenedUrl) {
        return ("redirect:/"+ getFullURL(shortenedUrl));
        //return Renderer.redirectTo(getFullURL(shortenedUrl)).build();
    }

    // in case of IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class) //
    public void illegalArgumentHandler() {
        //
    }

    public String mapShortURL(String fullURL){

        String shortURL = encodeURL(12);// Generate a 12-bit random string, or any length you want
        while (full_short.containsValue(shortURL)) { // make the shorted URL unique
            shortURL = encodeURL(12);
        }
        full_short.put(fullURL, shortURL);
        return shortURL;
    }

    public String encodeURL(int length){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < length; i++ ) {
            int number=random.nextInt(62); // length ^ 62
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
