package fr.anarchick.skriptpacket.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import fr.anarchick.skriptpacket.Logging;
import fr.anarchick.skriptpacket.elements.expressions.datawatcher.DataWatcher;
import fr.anarchick.skriptpacket.packets.PacketManager;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class Types {
    
    static {

        Logging.info("Register Skript types");

        if (Classes.getClassInfoNoError("packettype") == null) {
            Classes.registerClass(new ClassInfo<>(PacketType.class, "packettype")
                    .user("packet ?types?")
                    .name("PacketType")
                    .since("1.0")
                    .description("Represents the type of a packet from ProtocolLib")
                    .usage(PacketManager.getAllPacketTypeNames())
                    .examples("broadcast all packettypes")
                    .defaultExpression(new EventValueExpression<>(PacketType.class))
                    .supplier(PacketManager.PACKETTYPES)
                    .parser(new Parser<>() {

                        @Override
                        public boolean canParse(final @NotNull ParseContext context) {
                            return true;
                        }

                        @Override
                        @Nullable
                        public PacketType parse(final @NotNull String name, final @NotNull ParseContext context) {
                            return PacketManager.getPacketType(name);
                        }

                        @Override
                        public @NotNull String toVariableNameString(PacketType packetType) {
                            return PacketManager.getPacketName(packetType);
                        }

                        @Override
                        public @NotNull String toString(PacketType packetType, int flags) {
                            return PacketManager.getPacketName(packetType);
                        }

                    })
                    .serializer(new Serializer<>() {

                        @Override
                        public @NotNull Fields serialize(PacketType packetType) {
                            final Fields f = new Fields();
                            f.putObject("packetType", PacketManager.getPacketName(packetType));
                            return f;
                        }

                        @Override
                        public void deserialize(PacketType packetType, @NotNull Fields f) {
                            assert false;
                        }

                        @Override
                        public PacketType deserialize(@NotNull Fields f) throws StreamCorruptedException {
                            final String name = (String) f.getObject("packetType");

                            if (name != null) {
                                return PacketManager.getPacketType(name);
                            }

                            return null;
                        }

                        @Override
                        @Nullable
                        public PacketType deserialize(final @NotNull String s) {
                            return PacketManager.getPacketType(s);
                        }

                        @Override
                        public boolean mustSyncDeserialization() {
                            return false;
                        }

                        @Override
                        protected boolean canBeInstantiated() {
                            return false;
                        }

                    })
            );
        }

        if (Classes.getClassInfoNoError("packet") == null) {
            Classes.registerClass(new ClassInfo<>(PacketContainer.class, "packet")
                    .user("packets?")
                    .name("Packet")
                    .since("1.0")
                    .description("Represents a packet from ProtocolLib")
                    .usage("")
                    .examples("")
                    .defaultExpression(new EventValueExpression<>(PacketContainer.class))
                    .parser(new Parser<>() {

                        @Override
                        public boolean canParse(final @NotNull ParseContext context) {
                            return false;
                        }

                        @Override
                        @Nullable
                        public PacketContainer parse(final @NotNull String packet, final @NotNull ParseContext context) {
                            return null;
                        }

                        @Override
                        public @NotNull String toVariableNameString(PacketContainer packet) {
                            return packet.toString();
                        }

                        @Override
                        public @NotNull String toString(PacketContainer packet, int flags) {
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
        }

        if (Classes.getClassInfoNoError("datawatcher") == null) {
            Classes.registerClass(new ClassInfo<>(DataWatcher.class, "datawatcher")
                    .user("datawatchers?")
                    .name("Datawatcher")
                    .since("2.0")
                    .description("A data watcher is a list of index (=integer) associate with a value (=object)")
                    .usage("")
                    .examples("")
                    .parser(new Parser<>() {

                        @Override
                        public boolean canParse(final @NotNull ParseContext context) {
                            return false;
                        }

                        @Override
                        @Nullable
                        public DataWatcher parse(final @NotNull String data, final @NotNull ParseContext context) {
                            return null;
                        }

                        @Override
                        public @NotNull String toVariableNameString(DataWatcher data) {
                            return data.toJSON().toString();
                        }

                        @Override
                        public @NotNull String toString(DataWatcher data, int flags) {
                            return data.toJSON().toString();
                        }

                    })
            );
        }

    }
    
}
