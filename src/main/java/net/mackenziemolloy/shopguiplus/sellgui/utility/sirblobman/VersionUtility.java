package net.mackenziemolloy.shopguiplus.sellgui.utility.sirblobman;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.Server;

public final class VersionUtility {

    private static String minecraftVersion;

    static {
        String bukkitVersion = Bukkit.getBukkitVersion();
        if (bukkitVersion.contains("-pre") || bukkitVersion.contains("-rc")) {
            Logger logger = Bukkit.getLogger();
            logger.warning("[ShopGUIPlus-SellGUI] You are using a '-pre' or '-rc' version of spigot.");
            logger.warning("[ShopGUIPlus-SellGUI] Bugs may occur when using a preview version.");
        }
    }

    /**
     * @return The current Minecraft version of the server (Example: 1.16.5)
     */
    public static @NotNull String getMinecraftVersion() {
        if (minecraftVersion != null) {
            return minecraftVersion;
        }

        minecraftVersion = resolveMinecraftVersion();
        return minecraftVersion;
    }

    private static @NotNull String resolveMinecraftVersion() {
        try {
            Method method = Server.class.getMethod("getMinecraftVersion");
            Object result = method.invoke(Bukkit.getServer());
            if (result instanceof String) {
                String version = ((String) result).trim();
                if (!version.isEmpty()) {
                    return version;
                }
            }
        } catch (ReflectiveOperationException ignored) {
        }

        String bukkitVersion = Bukkit.getBukkitVersion();
        int firstDash = bukkitVersion.indexOf('-');
        String versionPart = (firstDash > 0 ? bukkitVersion.substring(0, firstDash) : bukkitVersion);

        if (versionPart.startsWith("1.")) {
            return versionPart;
        }

        return "1.21";
    }

    /**
     * @return The current NMS version of the server (Example: 1_16_R3)
     */
    public static @NotNull String getNetMinecraftServerVersion() {
        Server server = Bukkit.getServer();
        Class<? extends Server> serverClass = server.getClass();
        Package serverPackage = serverClass.getPackage();
        String serverPackageName = serverPackage.getName();

        int lastPeriodIndex = serverPackageName.lastIndexOf('.');
        int nextIndex = (lastPeriodIndex + 2);
        String nmsVersion = serverPackageName.substring(nextIndex);

        if (nmsVersion.matches("v1_\\d+_R\\d+")) {
            return nmsVersion;
        }

        return getMinecraftVersion();
    }

    /**
     * @return The current major. Minor version of the server (Example: 1.16)
     */
    public static @NotNull String getMajorMinorVersion() {
        return getMajorVersion() + "." + getMinorVersion();
    }

    /**
     * @return The current major version of the server as an integer (Example: {@code 1})
     */
    public static int getMajorVersion() {
        return parseVersionComponent(0);
    }

    /**
     * @return The current minor version of the server as an integer (Example: {@code 16})
     */
    public static int getMinorVersion() {
        return parseVersionComponent(1);
    }

    private static int parseVersionComponent(int index) {
        String minecraftVersion = getMinecraftVersion();
        String[] parts = minecraftVersion.split("\\.");

        if (parts.length <= index) {
            return 0;
        }

        String numericPart = parts[index].replaceAll("[^0-9].*", "");
        if (numericPart.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(numericPart);
    }
}
