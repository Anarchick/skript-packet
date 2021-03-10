# skript-packet
A Skript packet addon to replace ThatPacketAddon (which is not updated) with Skript 2.5.2+

# Requirements
 - Recent version of Skript
 - Stable ProtocolLib dev build (does not works with v-4.5.1)
 - Java8+
 - It's highly recommended to use skript-reflect

# What is a packet
The Minecraft server and your Minecraft client share information called "packets".
Example of a packet:  
 - Any movement made by an entity
 - Opening a gui to a player
 - When the player use an item

The interest of manipulation of packets is to send fake information to a specific group of players, like display a fake diamond block (client side) instead of a tnt (server side).
You can do a lot of things with packets, but it's really hard to understand how to use them ...

This link can help you to identify the content of a packet: https://wiki.vg/Protocol
 
# Example of code using skript-packet + skript-reflect ?

```applescript
packet event play_client_held_item_slot:
    broadcast "slot changed"
```


```applescript
function packetSearch(s: string):
    loop all packettypes:
        set {_packettype} to lowercase "%loop-value%"
        {_packettype} contain {_s}
        send formatted "<suggest command:%{_packettype}%>%{_packettype}%" to all players
```


```applescript
function unloadChunk(x: number, z: number, players: players):
        set {_packet} to new play_server_unload_chunk packet
        set field 0 of packet {_packet} to {_x}
        set field 1 of packet {_packet} to {_z}
        send {_players::*} packet {_packet}
```


```applescript
effect show block break animation [[stage] %-number%] of %block% for %players%:
    trigger: 
        set {_stage} to (round down expression-1) ? 0
        {_stage} is between 0 and 9
        set {_block} to expression-2
        set {_players::*} to expression-3
        set {_packet} to new play_server_block_break_animation packet
        set field 0 of packet {_packet} to id of (random entity out of all entities in world of {_block})
        set field 1 of packet {_packet} to nms block position from {_block}
        set field 2 of packet {_packet} to {_stage}
        send {_players::*} packet {_packet}
```


```applescript
import:
    net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange$a

effect change client side gamemode of %players% to %gamemode%:
    trigger:
        set {_packet} to new play_server_game_state_change packet
        set field 1 of packet {_packet} to 0 if "%expression-2%" is "survival"
        set field 1 of packet {_packet} to 1 if "%expression-2%" is "creative"
        set field 1 of packet {_packet} to 2 if "%expression-2%" is "adventure"
        set field 1 of packet {_packet} to 3 if "%expression-2%" is "spectator" 
        set field 0 of packet {_packet} to new a(3) # reason
        send packet {_packet} to expression-1
```


```applescript
import:
    net.minecraft.server.v1_16_R3.EnumItemSlot

effect client side equipment %string% of %entity% to %itemstack% for %players%:
    trigger:
        set {_slot} to expression-1
        {_slot} is "CHEST" or "FEET" or "HEAD" or "LEGS" or "MAINHAND" or "OFFHAND"
        set {_entity} to expression-2
        {_entity} is an entity
        set {_item} to expression-3 ? air
        set {_item} to nms of (random item out of {_item})
        set {_players::*} to expression-4
        set {_packet} to new play_server_entity_equipment packet
        set field 0 of packet {_packet} to id of {_entity}
        set {_itemSlot} to EnumItemSlot.HEAD
        set {_pair} to pair {_itemSlot} with {_item}
        set {_data} to {_pair} as arraylist
        set field 1 of packet {_packet} to {_data}
        send {_players::*} packet {_packet}
```
