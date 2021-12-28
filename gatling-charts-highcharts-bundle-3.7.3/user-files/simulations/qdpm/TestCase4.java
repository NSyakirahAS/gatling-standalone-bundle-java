package qdpm;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class TestCase4 extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost")
    .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0");
  
  private Map<CharSequence, String> headers_0 = Map.of("Upgrade-Insecure-Requests", "1");
  
  
  private Map<CharSequence, String> headers_1 = Map.ofEntries(
    Map.entry("Origin", "http://localhost"),
    Map.entry("Upgrade-Insecure-Requests", "1")
  );
  
  private Map<CharSequence, String> headers_2 = Map.ofEntries(
    Map.entry("Accept", "text/html, */*; q=0.01"),
    Map.entry("X-Requested-With", "XMLHttpRequest")
  );



  private ScenarioBuilder scn = scenario("TestCase4")
    // redirect
    .exec(
      http("Load login page")
        .get("/qdpm/index.php/")
        .headers(headers_0)
    )
    .pause(34)
    // userlogin
    .exec(
      http("User login")
        .post("/qdpm/index.php/login")
        .headers(headers_1)
        .formParam("login[email]", "syakirah.asata@gmail.com")
        .formParam("login[password]", "p@ssw0rd")
        .formParam("http_referer", "http://localhost/qdpm/index.php/")
    )
    .pause(19)
    // view project
    .exec(
      http("Load all projects page")
        .get("/qdpm/index.php/projects")
        .headers(headers_0)
    )
    .pause(23)
    // edit gatling project
    .exec(
      http("Load specific project page")
        .get("/qdpm/index.php/projects/edit/id/10")
        .headers(headers_2)
    )
    .pause(41)
    // user logout
    .exec(
      http("User logout")
        .get("/qdpm/index.php/login/logoff")
        .headers(headers_0)
    );

  {
    setUp(
  scn.injectClosed(
    constantConcurrentUsers(10).during(10), // 1
    rampConcurrentUsers(10).to(20).during(10) // 2
  ).protocols(httpProtocol)
);
  }
}
