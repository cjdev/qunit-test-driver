To get started, [add QunitTestDriver](./wiki/Maven-Setup) to your existing Maven project.  Next, create a JUnit test that extends QUnitTest, implementing the required methods:

    public class ExampleQUnitTest {
        @Test
        public String getTestPageUrl() {
            return QUnitTestDriver.run("path/to/qUnitTest.html");
        }
    }

QUnitTestDriver supports some additional configuration options that you can supply right inside of your Java test - No need to edit external configuration files such as XML or build scripts. Configuration is managed through the optional Varargs of Configuration objects like this:

    public class ExampleQUnitTest {
        @Test
        public String getTestPageUrl() {
            return QUnitTestDriver.run("path/to/qUnitTest.html", new Configuration(configurationOptions)...);
        }
    }

# Pausing The Test Just After The Server Starts
Javascript treats HTML loaded from a "file://" URL different from HTML loaded from an "HTTP://" URL. For this reason, QUnitTestDriver starts a lightweight Jetty server for each test. It's common, especially when first starting, to have trouble where exactly the test is relative to where Jetty started. This configuration option will cause the test to join to the Jetty server which prevents the test from completing on its own.

    @Test public void testQunitPage(){
        QUnitTestDriver.run("path/to/qUnitTest.html", new JoinToServer());
    }

# Changing The Port The Server Runs On
QUnitTestDriver is configured to try a fixed set of ports by default. Each time the internal server starts, a set of ports is consulted in order until the server is able to start. This is good if you use a CI system such as Jenkins that could run several tests in parallel on the same machine. You can configure the port or ports that are tried by passing in a new set.

    @Test public void testQunitPage(){
        QUnitTestDriver.run("path/to/qUnitTest.html", new PortSet(8080, 8081, 8082));
    }

# Changing The Server Root
Let's say you would like the server to host files from multiple directories...

    @Test public void testQunitPage(){
        QUnitTestDriver.run(
            "path/to/qUnitTest.html",             
            new PathMapping("/", "/some/path/on/the/file/system"),
            new PathMapping("/tests", "/another/path/on/the/file/system"),
            new PathMapping("/css", "/some/other/path/on/the/file/system"),
            new PathMapping("/html", "/last/one/i/swear/herp/derp")
        );
    }

# Emulating Different Browsers
QunitTestDriver? uses HTMLUnit under the covers which comes along with the ability to emulate different browsers. The default is Firefox 3.6, but you can choose a different browser like this:

    @Test public void testQunitPage(){ 
        QUnitTestDriver.run("path/to/qUnitTest.html", new EmulateBrowser(BrowserVersion.INTERNET_EXPLORER_8)); 
    }

Browsers available are listed in the HTMLUnit Documentation.
