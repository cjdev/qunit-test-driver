package cj.qunit;

public class PortUtil {
	public static int getPort(Properties props, int defaultPort){
		String prefix = props.getProperty("partition.port.prefix")
		if (prefix && !prefix.equals("null")) {
			return Integer.parseInt(prefix + 98)
		}
		return defaultPort;
	}
}
