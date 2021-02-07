package fr.anarchick.skriptpacket.elements.types;

import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import fr.anarchick.skriptpacket.packets.PacketManager;

public class Types {
	
	static {
		Classes.registerClass(new ClassInfo<>(PacketType.class, "packettype")
				.user("packet ?types?")
                .name("PacketType")
                .since("1.0")
                .description("Represents the type of a packet from ProtocolLib")
                //.examples("")
                .parser(new Parser<PacketType>() {
                	
                	@Override
					@Nullable
                	public PacketType parse(final String name, final ParseContext context) {
                		return PacketManager.getPacketType(name);
                	}
                	
                	@Override
					public boolean canParse(final ParseContext context) {
						return true;
					}
                	
					@Override
					public String getVariableNamePattern() {
						return "[a-zA-Z_]+";
					}

					@Override
					public String toString(PacketType packettype, int flags) {
						return PacketManager.getPacketName(packettype);
					}

					@Override
					public String toVariableNameString(PacketType packettype) {
						return PacketManager.getPacketName(packettype);
					}
					
                })
        );
		
		Classes.registerClass(new ClassInfo<>(PacketContainer.class, "packet")
				.user("packets?")
                .name("Packet")
                .since("1.0")
                .description("Represents a packet from ProtocolLib")
                //.examples("")
                .parser(new Parser<PacketContainer>() {
                	
                	@Override
					@Nullable
                	public PacketContainer parse(final String packet, final ParseContext context) {
                		return null;
                	}
                	
					@Override
					public String getVariableNamePattern() {
						return "PacketContainer\\[.*\\]";
					}

					@Override
					public String toString(PacketContainer packet, int flags) {
						packet.getAttributeCollectionModifier().getValues();
						return packet.toString();
					}

					@Override
					public String toVariableNameString(PacketContainer packet) {
						return packet.toString();
					}
					
                })
        );
	}
}
