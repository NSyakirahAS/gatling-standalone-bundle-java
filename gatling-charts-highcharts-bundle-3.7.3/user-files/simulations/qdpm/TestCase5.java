package qdpm;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class TestCase5 extends Simulation {


  // 1 HTTP Protocol Setup
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

  FeederBuilder.Batchable<String> loginFeeder =
  csv("loginDetails.csv").circular();

  FeederBuilder.Batchable<String> feed =
  csv("clientDetails.csv").random();


// 2 Scenario definition
ChainBuilder login =
  exec(
    http("Load login page")
    .get("/qdpm/index.php")
    .headers(headers_0))
  .pause(34);

 ChainBuilder userLogin = 
 feed(loginFeeder)
  .exec(
    http("User login")
    .post("/qdpm/index.php/login")
    .headers(headers_1)
    .formParam("username", "#{username}")
    .formParam("password", "#{password}")
    .formParam("user_group", "#{user_group}")
    .formParam("http_referer", "http://localhost/qdpm/index.php/")
    )
  .pause(19);

ChainBuilder viewProject =
  exec(
      http("Load all projects page")
        .get("/qdpm/index.php/projects")
        .headers(headers_0)
    )
    .pause(23);

ChainBuilder editProject =
  exec(
      http("Load specific project page")
        .get("/qdpm/index.php/projects/edit/id/10")
        .headers(headers_2)
    )
    .pause(41);

ChainBuilder logout =
  exec(
      http("User logout")
        .get("/qdpm/index.php/login/logoff")
        .headers(headers_0)
    );


  private ScenarioBuilder admin = scenario("Admin")
    .exec(login, userLogin, viewProject, editProject, logout);

  private ScenarioBuilder manager = scenario("Manager")
    .exec(login, userLogin, viewProject, logout);

  private ScenarioBuilder client = scenario("Client")
    .exec(login, userLogin, logout);


  // 3 Load simulation design
  {
    {
  setUp(
    admin.injectOpen(rampUsers(10).during(10)), // inject 10 users over 10 seconds
    manager.injectOpen(rampUsers(2).during(10)), // inject 2 users over 10 seconds
    client.injectOpen(rampUsers(10).during(10)) // inject 10 users over 10 seconds
  ).protocols(httpProtocol);
}
  }
}
