import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
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
        return (parts.length != 4 || Arrays.stream(parts).anyMatch(part -> {
            try {
                int octet = Integer.parseInt(part);
                return octet < 0 || octet > 255;
            } catch (NumberFormatException e) {
                return true;
            }
        })) ? -1 : Arrays.stream(parts).mapToInt(Integer::parseInt).reduce((result, octet) -> (result << 8) | octet).orElse(0);
    }


    public static int to32BitIp(int[] ip) {
        return (ip.length != 4 || Arrays.stream(ip).anyMatch(octet -> octet < 0 || octet > 255)) ? -1 : IntStream.of(ip).reduce((result, octet) -> (result << 8) | octet).orElse(0);
    }


    public static int getSuffix(String network) {
        String[] addressAndSuffix = network.split("/");
        return (addressAndSuffix.length != 2 || !toString(to32BitIp(addressAndSuffix[0])).equals(addressAndSuffix[0])) ? -1 : Integer.parseInt(addressAndSuffix[1]);
    }

    public static int getNetmask(String network) {
        int suffix = getSuffix(network);
        return (suffix == -1) ? -1 : ~0 << (32 - suffix);
    }

    public static int getNetwork(int ip, int suffix) {
        return (suffix < 0 || suffix > 32) ? -1 : (ip & (~0 << (32 - suffix)));
    }

    public static int getNetwork(String network) {
        String[] partsString = network.split("/");
        return (!toString(to32BitIp(partsString[0])).equals(partsString[0])) ? -1 : getNetwork(to32BitIp(partsString[0]), Integer.parseInt(partsString[1]));
    }

    public static int[] toIntArray(int ip) {
        return new int[]{ip >>> 24, (ip << 8) >>> 24, (ip << 16) >>> 24, (ip << 24) >>> 24};
    }

    public static String toString(int ip) {
        return String.join(".", IntStream.range(0, 4).mapToObj(i -> String.valueOf((ip >> (8 * (3 - i))) & 255)).toArray(String[]::new));
    }

    public static String toString(int network, int suffix) {
        return (toString(network).isEmpty() || suffix < 0 || suffix > 32) ? "" : toString(network) + "/" + suffix;
    }


    public static String toHexString(int ip) {
        return String.format("%02x.%02x.%02x.%02x", (ip >> 24) & 0xFF, (ip >> 16) & 0xFF, (ip >> 8) & 0xFF, ip & 0xFF);
    }

    public static String toHexString(int network, int suffix) {
        return (toHexString(network).isEmpty() || suffix < 0 || suffix > 32) ? "" : toHexString(network) + "/" + Integer.toHexString(suffix);
    }


    /*  public static int[] getNextNetworks(int network, int suffix, int n) {
          int[] networks = new int[n];

          if (suffix < 0 || suffix > 32) {
              return new int[0];
          }
          for (int i = 0; i < n; i++) {
              networks[i] = network + (i << (32 - suffix));
          }
          if (networks[0] < 0 && (network + ((n - 1) << (32 - suffix))) > 0)  {
              return new int[0];
          }
          return networks;
      }*/
    public static int[] getNextNetworks(int network, int suffix, int n) {
        return (suffix < 0 || suffix > 32 || (network < 0 && (network + ((n - 1) << (32 - suffix))) > 0)) ? new int[0] : IntStream.range(0, n).map(i -> network + (i << (32 - suffix))).toArray();
    }

 /*   public static int[] getAllNetworksForNewSuffix(int network, int suffix, int newSuffix) {
        if (suffix < 0 || suffix > 32 || newSuffix < 0 || newSuffix > 32) {
            return new int[0];
        }
        int mask = ~0 << (32 - newSuffix);
        int baseNetwork = network & mask;
        int numNetworks = 1 << (newSuffix - suffix);
        int[] networks = new int[numNetworks];
        for (int i = 0; i < numNetworks; i++) {
            networks[i] = baseNetwork + (i << (32 - newSuffix));
        }
        if (networks[0] < 0 && networks[numNetworks-1]> 0)  {
            return new int[0];
        }
        return networks;
    }*/

    public static int[] getAllNetworksForNewSuffix(int network, int suffix, int newSuffix) {
        return (suffix < 0 || suffix > 32 || newSuffix < 0 || newSuffix > 32 || (network < 0 && (network + ((1 << (newSuffix - suffix)) - 1) << (32 - newSuffix)) > 0)) ? new int[0] : IntStream.range(0, 1 << (newSuffix - suffix)).map(i -> (network & (~0 << (32 - newSuffix))) + (i << (32 - newSuffix))).toArray();
    }


    /*public static int[] getAllIpsInNetwork(int network, int suffix) {
        if (suffix < 0 || suffix > 32) {
            return new int[0];
        }
        int numAddresses = 1 << (32 - suffix);
        int[] ips = new int[numAddresses];
        for (int i = 0; i < numAddresses; i++) {
            ips[i] = network + i;
        }
        return ips;
    }*/
    public static int[] getAllIpsInNetwork(int network, int suffix) {
        return (suffix < 0 || suffix > 32) ? new int[0] : IntStream.range(0, 1 << (32 - suffix)).map(i -> network + i).toArray();
    }

    public static boolean isInNetwork(String ip, String network) {
        return (to32BitIp(ip) & getNetwork(network) & (~0 << (32 - getSuffix(network)))) == getNetwork(network);
    }

}
