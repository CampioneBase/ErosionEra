package campionebase.erosionera.network;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.network.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BioMachineryNetwork {
    public static final Logger LOGGER = LogManager.getLogger(BioMachineryNetwork.class);
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
                BioNetConnectingPacket.Request.class,
                BioNetConnectingPacket.Request::encode,
                BioNetConnectingPacket.Request::decode,
                BioNetConnectingPacket.Request::handle);
        INSTANCE.registerMessage(PACKET_ID++,
                BioNetConnectingPacket.Response.class,
                BioNetConnectingPacket.Response::encode,
                BioNetConnectingPacket.Response::decode,
                BioNetConnectingPacket.Response::handle);

        INSTANCE.registerMessage(PACKET_ID++,
                BioCameraListPacket.Request.class,
                BioCameraListPacket.Request::encode,
                BioCameraListPacket.Request::decode,
                BioCameraListPacket.Request::handle);
        INSTANCE.registerMessage(PACKET_ID++,
                BioCameraListPacket.Response.class,
                BioCameraListPacket.Response::encode,
                BioCameraListPacket.Response::decode,
                BioCameraListPacket.Response::handle);

        INSTANCE.registerMessage(PACKET_ID++,
                BioCameraOccupationPacket.Request.class,
                BioCameraOccupationPacket.Request::encode,
                BioCameraOccupationPacket.Request::decode,
                BioCameraOccupationPacket.Request::handle);
        INSTANCE.registerMessage(PACKET_ID++,
                BioCameraOccupationPacket.Response.class,
                BioCameraOccupationPacket.Response::encode,
                BioCameraOccupationPacket.Response::decode,
                BioCameraOccupationPacket.Response::handle);

        INSTANCE.registerMessage(PACKET_ID++,
                BioCameraNamingPacket.class,
                BioCameraNamingPacket::encode,
                BioCameraNamingPacket::decode,
                BioCameraNamingPacket::handle);

        INSTANCE.registerMessage(PACKET_ID++,
                BioCameraActionPacket.class,
                BioCameraActionPacket::encode,
                BioCameraActionPacket::decode,
                BioCameraActionPacket::handle);

        INSTANCE.registerMessage(PACKET_ID++,
                BioCameraAlivePacket.class,
                BioCameraAlivePacket::encode,
                BioCameraAlivePacket::decode,
                BioCameraAlivePacket::handle);

        INSTANCE.registerMessage(PACKET_ID++,
                BioControllerReleasePacket.class,
                BioControllerReleasePacket::encode,
                BioControllerReleasePacket::decode,
                BioControllerReleasePacket::handle);
    }
}
