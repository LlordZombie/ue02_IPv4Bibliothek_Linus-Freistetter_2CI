import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		//TODO errorhandling
		String ip = "192.168.0.1";
		int[] ipArray = {192, 168, 0, 1};
		String network = "192.168.0.0/16";
		int suffix = 26;
		int netmask = getNetmask(network);
		int ipInt = to32BitIp(ip);
		int[] nextNetworks = getNextNetworks(ipInt, suffix, 4);
		int[] nextNetworksForNewSuffix = getAllNetworksForNewSuffix(to32BitIp("10.0.0.0"), 8, 10);
		int[] allIPsInNetwork = getAllIpsInNetwork(ipInt, 30);

		System.out.println("to32BitIp: " + Integer.toHexString(to32BitIp(ip)));
		System.out.println("to32BitIp (Array): " + Integer.toHexString(to32BitIp(ipArray)));
		System.out.println("getSuffix: " + getSuffix(network));
		System.out.println("getNetmask: " + Integer.toHexString(netmask));
		System.out.println("getNetwork: " + Integer.toHexString(getNetwork(to32BitIp(ip), suffix)));
		System.out.println("getNetwork (String): " + Integer.toHexString(getNetwork(network)));
		System.out.println("toIntArray: " + Arrays.toString(toIntArray(ipInt)));
		System.out.println("toString: " + toString(ipInt));
		System.out.println("toString (with suffix): " + toString(ipInt, suffix));
		System.out.println("toHexString: " + toHexString(ipInt));
		System.out.println("toHexString (with suffix): " + toHexString(ipInt, suffix));
		for (int nextNetwork : nextNetworks) {
			System.out.print(toString(nextNetwork) + ", ");
		}
		System.out.println();
		for (int networksForNewSuffix : nextNetworksForNewSuffix) {
			System.out.print(toString(networksForNewSuffix) + ", ");
		}
		System.out.println();
		for (int j : allIPsInNetwork) {
			System.out.print(toString(j) + ", ");
		}
		System.out.println();
		System.out.println("isInNetwork: " + isInNetwork(ip, network));
	}

	public static int to32BitIp(String ip) {
		String[] parts = ip.split("\\.");
		if (parts.length != 4) {
			return -1;
		}
		int result = 0;
		for (String part : parts) {
			try {
				int octet = Integer.parseInt(part);
				if (octet < 0 || octet > 255) {
					return -1;
				}
				result = (result << 8) | octet;
			} catch (NumberFormatException e) {
				return -1;
			}

		}
		return result;
	}

	public static int to32BitIp(int[] ip) {
		if (ip.length != 4) {
			return -1;
		}
		int result = 0;
		for (int octet : ip) {
			if (octet < 0 || octet > 255) {
				return -1;
			}
			result = (result << 8) | octet;
		}
		return result;
	}

	public static int getSuffix(String network) {
		int rInt;
		String[] addressAndSuffix = network.split("/");
		if (addressAndSuffix.length != 2) {
			return -1;
		}
		if (to32BitIp(addressAndSuffix[0]) == -1) {
			return -1;
		}
		rInt = Integer.parseInt(addressAndSuffix[1]);
		return rInt;
	}

	public static int getNetmask(String network) {
		int suffix = getSuffix(network);
		if (suffix == -1) {
			return -1;
		}
		return ~0 << (32 - suffix);
	}

	public static int getNetwork(int ip, int suffix) {
		if (suffix < 0 || suffix > 32) {
			return -1;
		}
		return ip & (~0 << (32 - suffix));
	}

	public static int getNetwork(String network) {
		String[] partsString = network.split("/");
		if (!String.valueOf(to32BitIp(partsString[0])).equals(partsString[0])){
			return -1;
		}
		return getNetwork(to32BitIp(partsString[0]), Integer.parseInt(partsString[1]));
	}

	public static int[] toIntArray(int ip) {
		int[] rInt = new int[4];
		if (ip == -1) {
			return new int[0];
		}
		for (int i = 3; i >= 0; i--) {
			rInt[3 - i] = (ip >>> (i * 8)) & 0xFF;
		}
		return rInt;
	}

	public static String toString(int ip) {
		int[] ipArray = toIntArray(ip);
		try {
			return String.format("%d.%d.%d.%d", ipArray[0], ipArray[1], ipArray[2], ipArray[3]);
		} catch (ArrayIndexOutOfBoundsException e) {
			return "";
		}
	}

	public static String toString(int network, int suffix) {
		String ipString = toString(network);
		if (ipString.isEmpty()) {
			return "";
		} else {
			return ipString + "/" + suffix;
		}
	}

	public static String toHexString(int ip) {
		int[] ipArray = toIntArray(ip);
		try {
			return String.format("%02x.%02x.%02x.%02x", ipArray[0], ipArray[1], ipArray[2], ipArray[3]);
		} catch (ArrayIndexOutOfBoundsException e) {
			return "";
		}
	}

	public static String toHexString(int network, int suffix) {
		String ipString = toHexString(network);
		if (ipString.isEmpty()) {
			return "";
		}
		return ipString + "/" + suffix;
	}

	public static int[] getNextNetworks(int network, int suffix, int n) {
		int[] networks = new int[n];
		if (network == -1) {
			return new int[0];
		}
		for (int i = 0; i < n; i++) {
			networks[i] = network + (i << (32 - suffix));
		}
		return networks;
	}

	public static int[] getAllNetworksForNewSuffix(int network, int suffix, int newSuffix) {
		if (network == -1) {
			return new int[0];
		}
		int mask = ~0 << (32 - newSuffix);
		int baseNetwork = network & mask;
		int numNetworks = 1 << (newSuffix - suffix);
		int[] networks = new int[numNetworks];
		for (int i = 0; i < numNetworks; i++) {
			networks[i] = baseNetwork + (i << (32 - newSuffix));
		}
		return networks;
	}

	public static int[] getAllIpsInNetwork(int network, int suffix) {
		if (network == -1) {
			return new int[0];
		}
		int numAddresses = 1 << (32 - suffix);
		int[] ips = new int[numAddresses];
		for (int i = 0; i < numAddresses; i++) {
			ips[i] = network + i;
		}
		return ips;
	}

	public static boolean isInNetwork(String ip, String network) {
		int ipInt = to32BitIp(ip);
		int networkInt = getNetwork(network);
		int suffix = getSuffix(network);
		int mask = ~0 << (32 - suffix);
		return (ipInt & mask) == networkInt;
	}
}
