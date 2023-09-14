public class Main {
	public static void main(String[] args) {
		// Beispielverwendung der Methoden
		String ip = "192.168.0.1";
		int[] ipArray = {192, 168, 0, 1};
		String network = "192.168.0.0/16";
		int suffix = 16;
		int netmask = getNetmask(network);

		System.out.println("to32BitIp: " + Integer.toUnsignedLong(to32BitIp(ip)));
		System.out.println("to32BitIp (Array): " + Integer.toUnsignedLong(to32BitIp(ipArray)));
		System.out.println("getSuffix: " + getSuffix(network));
		System.out.println("getNetmask: " + Integer.toHexString(netmask));
		//System.out.println("getNetwork: " + Integer.toHexString(getNetwork(ip, suffix)));
		//System.out.println("getNetwork (String): " + Integer.toHexString(getNetwork(network)));
		//System.out.println("toIntArray: " + Arrays.toString(toIntArray(ipInt)));
		//System.out.println("toString: " + toString(ipInt));
		//System.out.println("toString (with suffix): " + toString(ipInt, suffix));
		//System.out.println("toHexString: " + toHexString(ipInt));
		//System.out.println("toHexString (with suffix): " + toHexString(ipInt, suffix));
		//System.out.println("getNextNetworks: " + Arrays.toString(getNextNetworks(ipInt, suffix, 4)));
		//System.out.println("getAllNetworksForNewSuffix: " + Arrays.toString(getAllNetworksForNewSuffix(ipInt, suffix, 20)));
		//System.out.println("getAllIpsInNetwork: " + Arrays.toString(getAllIpsInNetwork(ipInt, suffix)));
		//System.out.println("isInNetwork: " + isInNetwork("10.1.2.3", "10.0.0.0/8"));
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

	public static int getSuffix(String network){
		int rInt;
		String[] addressAndSuffix = network.split("/");
		if (addressAndSuffix.length!= 2){
			return -1;
		}
		if (to32BitIp(addressAndSuffix[0])==-1){
			return -1;
		}
		rInt=Integer.parseInt(addressAndSuffix[1]);
		return rInt;
	}
	public static int getNetmask(String network){
		int suffix= getSuffix(network);
		return ~0 << (32 - suffix);
	}

	public static int getNetwork(String network){

	}

}
