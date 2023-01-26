package fr.anarchick.skriptpacket.elements;

import fr.anarchick.skriptpacket.SkriptPacket;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import fr.anarchick.skriptpacket.elements.expressions.datawatcher.DataWatcher;
import fr.anarchick.skriptpacket.packets.PacketManager;

public class Types {
    
    static {
        if (Classes.getClassInfoNoError("packettype") == null) Classes.registerClass(new ClassInfo<>(PacketType.class, "packettype")
                .user("packet ?types?")
                .name("PacketType")
                .since("1.0")
                .description("Represents the type of a packet from ProtocolLib")
                //.examples("")
                .parser(new Parser<>() {
                    
                    @Override
                    public boolean canParse(final ParseContext context) {
                        return true;
                    }
                    
                    @Override
                    @Nullable
                    public PacketType parse(final String name, final ParseContext context) {
                        return PacketManager.getPacketType(name);
                    }

                    @Override
                    public String toVariableNameString(PacketType packettype) {
                        return PacketManager.getPacketName(packettype);
                    }
                    
                    @Override
                    public String toString(PacketType packettype, int flags) {
                        return PacketManager.getPacketName(packettype);
                    }
                    
                })
        );
        
        if (Classes.getClassInfoNoError("packet") == null) Classes.registerClass(new ClassInfo<>(PacketContainer.class, "packet")
                .user("packets?")
                .name("Packet")
                .since("1.0")
                .description("Represents a packet from ProtocolLib")
                //.examples("")
                .parser(new Parser<>() {
                    
                    @Override
                    public boolean canParse(final ParseContext context) {
                        return false;
                    }
                    
                    @Override
                    @Nullable
                    public PacketContainer parse(final String packet, final ParseContext context) {
                        return null;
                    }
                    
                    @Override
                    public String toVariableNameString(PacketContainer packet) {
                        return packet.toString();
                    }
                    
                    @Override
                    public String toString(PacketContainer packet, int flags) {
                        String str;
                        try {
                            str = packet.toString();
                        } catch (Exception e) {
                            str = "PacketContainer[type=" + packet.getType() + ", structureModifier=INVALID_DATA]";
                        }
                        return str;
                    }
                    
                })
        );
        
        if (Classes.getClassInfoNoError("datawatcher") == null) Classes.registerClass(new ClassInfo<>(DataWatcher.class, "datawatcher")
                .user("datawatchers?")
                .name("Datawatcher")
                .since("2.0")
                .description("A data watcher is a list of index (=integer) associate with a value (=object)")
                //.examples("")
                .parser(new Parser<>() {
                    
                    @Override
                    public boolean canParse(final ParseContext context) {
                        return false;
                    }
                    
                    @Override
                    @Nullable
                    public DataWatcher parse(final String data, final ParseContext context) {
                        return null;
                    }
                    
                    @Override
                    public String toVariableNameString(DataWatcher data) {
                        return data.toJSON().toString();
                    }
                    
                    @Override
                    public String toString(DataWatcher data, int flags) {
                        return data.toJSON().toString();
                    }
                    
                })
        );
    }
    
}
