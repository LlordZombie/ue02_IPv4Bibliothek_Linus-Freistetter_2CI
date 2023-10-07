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

    /**
     * diese methode verwandelt eine ip in ddn in eine einzelne integer mit demselben wert <br>sollte ip keine valide ip addresse sein, gibt die methode -1 zurück
     *
     * @param ip ein string, der eine ipadresse in ddn darstellen soll
     * @return eine integer, die denselben wert wie die ip in ddn haben sollte; im falle eines fehlers -1
     */
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

    /**
     * diese methode verwandelt ein array in eine einzelne integer mit demselben wert wie alle oktette hintereinander <br>sollte ein wert in ip keine valides oktet sein oder die länge des arrays nicht passen, gibt die methode -1 zurück
     *
     * @param ip ein array, dessen 4 stellen die oktette einer ipadresse darstellen sollen
     * @return eine integer, die denselben wert wie die oktette in eine ip verpackthaben sollte; im falle eines fehlers -1
     */
    public static int to32BitIp(int[] ip) {
        return (ip.length != 4 || Arrays.stream(ip).anyMatch(octet -> octet < 0 || octet > 255)) ? -1 : IntStream.of(ip).reduce((result, octet) -> (result << 8) | octet).orElse(0);
    }

    /**
     * diese methode holt sich den suffix aus einer netzadresse.
     *
     * @param network die netzwerkadresse, aus der der suffix extrahiert wird
     * @return ein suffix, sollte etwas prüfbares nicht funktionieren gibt es -1 zurück
     */
    public static int getSuffix(String network) {
        String[] addressAndSuffix = network.split("/");
        return (addressAndSuffix.length != 2 || !toString(to32BitIp(addressAndSuffix[0])).equals(addressAndSuffix[0]) || !addressAndSuffix[1].matches("\\d+")) ? -1 : Integer.parseInt(addressAndSuffix[1]);
    }

    /**
     * holt sich aus dem suffix von network die netmask
     *
     * @param network eine ip in ddn inkl. suffix
     * @return die netmask; wenn suffix nicht passt -1
     */
    public static int getNetmask(String network) {
        int suffix = getSuffix(network);
        return (suffix < 0 || suffix > 32) ? -1 : ~0 << (32 - suffix);
    }

    /**
     * holt sich die netzadresse aus ip und suffix
     *
     * @param ip     die ip, deren netzadresse gesucht wird
     * @param suffix die snm für das netzwerk
     * @return die netzadresse als int; im falle eines problems mit suffix -1
     */
    public static int getNetwork(int ip, int suffix) {
        return (suffix < 0 || suffix > 32) ? -1 : (ip & (~0 << (32 - suffix)));
    }

    /**
     * holtr sich die netzaddresse aus einem string mit ip und suffix in ddn
     *
     * @param network ip und suffix in ddn
     * @return die netzadresse als integer
     */
    public static int getNetwork(String network) {
        String[] partsString = network.split("/");
        return (!toString(to32BitIp(partsString[0])).equals(partsString[0])) ? -1 : getNetwork(to32BitIp(partsString[0]), Integer.parseInt(partsString[1]));
    }

    /**
     * verwandelt eine beliebige int in ein array aus 4 oktetten
     *
     * @param ip die int die verwandelt wird
     * @return ein array aus 4 oktetten
     */
    public static int[] toIntArray(int ip) {
        return new int[]{ip >>> 24, (ip << 8) >>> 24, (ip << 16) >>> 24, (ip << 24) >>> 24};
    }

    /**
     * verwandelt eine beliebige int in eine ip in ddn
     *
     * @param ip die int die verwandelt wird
     * @return ein string mit 4 oktetten in ddn
     */
    public static String toString(int ip) {
        return String.join(".", IntStream.range(0, 4).mapToObj(i -> String.valueOf((ip >> (8 * (3 - i))) & 255)).toArray(String[]::new));
    }

    /**
     * verwandelt eine beliebige int in eine ip in ddn; inkl. suffix
     *
     * @param network die int die verwandelt wird
     * @param suffix  der suffix, der hinten angehängt wird
     * @return ein string mit 4 oktetten in ddn; inkl. suffix; im falle eines fehlers ein leerer string
     */
    public static String toString(int network, int suffix) {
        return (toString(network).isEmpty() || suffix < 0 || suffix > 32) ? "" : toString(network) + "/" + suffix;
    }

    /**
     * verwandelt eine beliebige int in eine ip in ddn (hexadezimal)
     *
     * @param ip die int die verwandelt wird
     * @return ein string mit 4 oktetten in ddn (hexadezimal)
     */
    public static String toHexString(int ip) {
        return String.format("%02x.%02x.%02x.%02x", (ip >> 24) & 0xFF, (ip >> 16) & 0xFF, (ip >> 8) & 0xFF, ip & 0xFF);
    }

    /**
     * verwandelt eine beliebige int in eine ip in ddn (hexadezimal); inkl. suffix
     *
     * @param network die int die verwandelt wird
     * @param suffix  der suffix, der hinten angehängt wird
     * @return ein string mit 4 oktetten in ddn; inkl. suffix; im falle eines fehlers ein leerer string; hexadezimal
     */
    public static String toHexString(int network, int suffix) {
        return (toHexString(network).isEmpty() || suffix < 0 || suffix > 32) ? "" : toHexString(network) + "/" + Integer.toHexString(suffix);
    }

    /**
     * sucht die nächsten n netzwerke für snm suffix, beginnend bei network; sollten weniger als n Netzwerke verfügbar sein, gibt die Methode ein leeres Array zurück
     *
     * @param network das netzwerk, von dem die methode ausgeht
     * @param suffix die snm für die netzwerke
     * @param n die anzahl der netzwerke
     * @return ein array mit den netzadressen der neuen netzwerke
     */
    public static int[] getNextNetworks(int network, int suffix, int n) {
        return (suffix < 0 || suffix > 32 || (network < 0 && (network + ((n - 1) << (32 - suffix))) > 0)) ? new int[0] : IntStream.range(0, n).map(i -> network + (i << (32 - suffix))).toArray();
    }

    /**
     * Die Methode liefert alle Netzwerke, die durch eine Vergrößerung des Suffix
     * auf newSuffix möglich sind
     * @param network die netzadresse, von der man ausgeht
     * @param suffix der suffix, den network zuerst hatte
     * @param newSuffix der neue suffix
     * @return ein array mit allen neuen netzadressen
     */
    public static int[] getAllNetworksForNewSuffix(int network, int suffix, int newSuffix) {
        return (suffix < 0 || suffix > 32 || newSuffix < 0 || newSuffix > 32 || newSuffix>suffix|| (network < 0 && (network + ((1 << (newSuffix - suffix)) - 1) << (32 - newSuffix)) > 0)) ? new int[0] : IntStream.range(0, 1 << (newSuffix - suffix)).map(i -> (network & (~0 << (32 - newSuffix))) + (i << (32 - newSuffix))).toArray();
    }

    /**
     * Die Methode liefert alle Adressen im Netzwerk mit dem angegebene Suffix
     * (von der Netzwerk-Adresse bis zur Broadcast-Adresse).
     * @param network die netzadresse
     * @param suffix die snm
     * @return ein array mit allen ips im netzwerk
     */
    public static int[] getAllIpsInNetwork(int network, int suffix) {
        return (suffix < 0 || suffix > 32) ? new int[0] : IntStream.range(0, 1 << (32 - suffix)).map(i -> network + i).toArray();
    }

    /**
     * ist eine IP-Adresse in einem Netzwerk enthalten
     * @param ip die ip, deren zugehörigkeit geprüft wird
     * @param network das netzwerk, in dem ip vorhanden ist
     * @return true, wenn ip in network ist
     */
    public static boolean isInNetwork(String ip, String network) {
        return (to32BitIp(ip) & getNetwork(network) & (~0 << (32 - getSuffix(network)))) == getNetwork(network);
    }

}
