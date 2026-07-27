package campionebase.erosionera.network;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.network.packet.BioNetConnectedBlocksPacket;
import campionebase.erosionera.network.packet.UpdateBioCameraListPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class BioMachineryNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    private static int PACKET_ID = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, "bio_machinery"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register(){
        INSTANCE.registerMessage(PACKET_ID++,
                BioNetConnectedBlocksPacket.Request.class,
                BioNetConnectedBlocksPacket.Request::encode,
                BioNetConnectedBlocksPacket.Request::decode,
                BioNetConnectedBlocksPacket.Request::handle);
        INSTANCE.registerMessage(PACKET_ID++,
                BioNetConnectedBlocksPacket.Response.class,
                BioNetConnectedBlocksPacket.Response::encode,
                BioNetConnectedBlocksPacket.Response::decode,
                BioNetConnectedBlocksPacket.Response::handle);

        INSTANCE.registerMessage(PACKET_ID++,
                UpdateBioCameraListPacket.Request.class,
                UpdateBioCameraListPacket.Request::encode,
                UpdateBioCameraListPacket.Request::decode,
                UpdateBioCameraListPacket.Request::handle);
        INSTANCE.registerMessage(PACKET_ID++,
                UpdateBioCameraListPacket.Response.class,
                UpdateBioCameraListPacket.Response::encode,
                UpdateBioCameraListPacket.Response::decode,
                UpdateBioCameraListPacket.Response::handle);
    }
}