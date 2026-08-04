package campionebase.erosionera.network;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class CameraOccupation {
    private final UUID playerUUID;
    private final String playerName;
    public float yaw;
    public float pitch;
    public int timestamp;

    public CameraOccupation(Player player, int tick){
        this.playerName = player.getName().getString();
        this.playerUUID = player.getUUID();
        this.yaw = 0f;
        this.pitch = 0f;
        this.timestamp = tick;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}
