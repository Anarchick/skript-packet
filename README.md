[![SkriptHubViewTheDocs](http://skripthub.net/static/addon/ViewTheDocsButton.png)](http://skripthub.net/docs/?addon=skript-packet)
[![Discord Banner 2](https://discordapp.com/api/guilds/138464183946575874/widget.png?style=banner2)](https://discord.com/channels/138464183946575874/860221632852393996)

# skript-packet
A Skript packet addon to replace ThatPacketAddon (which is not updated) with Skript 2.5.2+

# CAUTIONS
⚠️ Skript-Packet **is not** a fork of TPA, syntaxes may change ⚠️  
⚠️ This plugin **is not** for beginners ⚠️  
⚠️ You may have to use java NMS wich is not an API ⚠️  
⚠️ You **should not** used packet for the first solution if possible, do not try to use this plugin to do ScoreBoard, BossBars, Particles, or existing things ⚠️

# Requirements
 - Recent version of Skript
 - Stable [ProtocolLib dev build](https://ci.dmulloy2.net/job/ProtocolLib/) (does not works with v-5.1.0)
 - Java8+
 - It's highly recommended to use skript-reflect
 - I have only tested mc 1.20.4 **but** should works in a lot of mc versions

# What is a packet
The Minecraft server and your Minecraft client share information called "packets".
Example of a packet:  
 - Any movement made by an entity
 - Opening a gui to a player
 - When the player use an item

The interest of manipulation of packets is to send fake information to a specific group of players, like display a fake diamond block (client side) instead of a tnt (server side).
You can do a lot of things with packets, but it's really hard to understand how to use them ...

- This link can help you to identify the content of a packet: https://wiki.vg/Protocol
- This link can help you to identify the arguments of a packet: https://minidigger.github.io/MiniMappingViewer/
- The wiki is a good start : https://github.com/Anarchick/skript-packet/wiki/Examples
 
# Example of code using skript-packet

```applescript
packet event play_client_held_item_slot:
    broadcast "slot changed"
    
on packet event play_server_chat:
    cancel event if "%field 0 of event-packet%" contain "block.minecraft.set_spawn"
```

<details>
  <summary>Example of 1.16.X hight level coding</summary>
 
```applescript
function BiomeStorage(biome: biome) :: object:
    set {_id} to nms biome id of {_biome}
    if {BiomeStorage::%{_id}%} is not set:
        loop 1024 times:
            set {_biomeId::%loop-value%} to {_id}
        set {BiomeStorage::%{_id}%} to {_biomeId::*} as primitive int array
    return {BiomeStorage::%{_id}%}

import:
    net.minecraft.server.v1_16_R3.PacketPlayOutMapChunk

effect change client side biome of [chunk] %chunk% to %biome% for %players% :
    trigger:
        await 0.toString() # Async
        set {_chunk} to expression-1
        set {_MapChunk} to new PacketPlayOutMapChunk(nms chunk of {_chunk}, 65535)
        set {_packet} to new play_server_map_chunk packet
        set field 0 of {_packet} to {_chunk}.getX()
        set field 1 of {_packet} to {_chunk}.getZ()
        set field 2 of {_packet} to {_MapChunk}.c # int
        set field 3 of {_packet} to {_MapChunk}.d # NBTTagCompound
        set field 4 of {_packet} to BiomeStorage(expression-2)
        set {_byte::*} to ...{_MapChunk}.f
        set field 5 of {_packet} to {_byte::*} # Primitive byte array
        set field 6 of {_packet} to {_MapChunk}.g # Represent all Tiles Entities
        set field 7 of {_packet} to true # Represent a full chunk, biomes are store only if true
        set field 8 of {_packet} to true
        set field 9 of {_packet} to {_} # Empty ArrayList
        send packet {_packet} to expression-3 without calling event
        
command /biome [<biome>] [<int>]:
    permission: fakebiome.cmd
    trigger:
        delete {biome}
        if arg-1 is set:
            set {biome} to arg-1
            BiomeStorage({biome})
        send "Fake biome set to %{biome}%" to sender

on async packet event play_server_map_chunk:
    {biome} is set
    field 7 of event-packet is true # Represent a full chunk, biomes are store only if true
    set field 4 of event-packet to BiomeStorage({biome})
```
</details>

 **More examples on the [wiki](https://github.com/Anarchick/skript-packet/wiki/Examples)** 
