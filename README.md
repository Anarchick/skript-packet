[![SkriptHubViewTheDocs](http://skripthub.net/static/addon/ViewTheDocsButton.png)](http://skripthub.net/docs/?addon=skript-packet)

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
This link can help you to identify the arguments of a packet: https://minidigger.github.io/MiniMappingViewer/
 
# Example of code using skript-packet + skript-reflect (FOR 1.16.x)

```applescript
packet event play_client_held_item_slot:
    broadcast "slot changed"
    
on packet event play_server_chat:
    cancel event if "%field 0 of event-packet%" contain "block.minecraft.set_spawn"
```

```applescript
function packetSearch(s: string):
    loop all packettypes:
        set {_packettype} to lowercase "%loop-value%"
        {_packettype} contain {_s}
        send formatted "<suggest command:%{_packettype}%>%{_packettype}%" to all players
```

```applescript
function fakeBlock(loc: location, toBlock: itemtype, players: players):
    set {_packet} to new play_server_block_change packet
    set field 0 of {_packet} to nms block position from {_loc}
    set field 1 of {_packet} to nms block from {_toBlock}
    send {_players::*} packet {_packet}
```

```applescript
function unloadChunk(x: number, z: number, players: players):
    set {_packet} to new play_server_unload_chunk packet
    set field 0 of {_packet} to {_x}
    set field 1 of {_packet} to {_z}
    send {_players::*} packet {_packet}
```

```applescript
on packet event play_client_steer_vehicle:
    set {_sideways} to field 0
    set {_forward} to field 1
    set {_jump} to field 2
    set {_unmount} to field 3
```

```applescript
# DO NOT CHANGE FOV WHEN PLAYER WALK/FLY SPEED IS MODIFIED
on packet event play_server_abilities:
    set field 5 to {%event-player%::FOV}

function FOV(p: players, x: number):
    set {_packet} to new play_server_abilities packet
    set field 5 of {_packet} to {_x}
    loop {_p::*}:
        set {_player} to loop-value
        set {%{_player}%::FOV} to {_x}
        send {_player} packet {_packet}
```

```applescript
function packetActionBar(text: text, receivers: players):
    set {_packet} to new play_server_chat packet
    set field 2 of {_packet} of {_packet} to basecomponent from text {_text}
    set field 3 of {_packet} to enum "GAME_INFO" from nms class "ChatMessageType"
    send packet {_packet} to {_receivers::*} without calling event
```

```applescript
# Use : packet update {_entity} for all players in world of {_entity}
# To reveal the hidden entity
function HideEntity(e: entities, p: players):
    loop {_e::*}:
        add entity id of loop-value to {_id::*}
    set {_packet} to new play_server_entity_destroy packet
    set field 0 of {_packet} to {_id::*} as primitive int array
    send {_p::*} packet {_packet}
```


```applescript
function breakAnimation(block: block, stage: number, players: players):
    set {_stage} to (round down {_stage}) ? 0
    {_stage} is between 0 and 9
    set {_packet} to new play_server_block_break_animation packet
    set field 0 of {_packet} to random integer between 0 and 999999
    set field 1 of {_packet} to nms block position from {_block}
    set field 2 of {_packet} to {_stage} as int # Convert to Integer is faster
    send {_players::*} packet {_packet}

effect show block break animation [[stage] %-number%] of %block% for %players%:
    trigger:
        set {_stage} to (round down expression-1) ? 0
        {_stage} is between 0 and 9
        set {_block} to expression-2
        set {_players::*} to expression-3
        set {_packet} to new play_server_block_break_animation packet
        set field 0 of {_packet} to random integer between 0 and 999999
        set field 1 of {_packet} to nms block position from {_block}
        set field 2 of {_packet} to {_stage} as int # Convert to Integer is faster
        send {_players::*} packet {_packet}
```

```applescript
import:
    net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange$a

effect change client side gamemode of %players% to %gamemode%:
    trigger:
        set {_packet} to new play_server_game_state_change packet
        set field 1 of {_packet} to 0 if "%expression-2%" is "survival"
        set field 1 of {_packet} to 1 if "%expression-2%" is "creative"
        set field 1 of {_packet} to 2 if "%expression-2%" is "adventure"
        set field 1 of {_packet} to 3 if "%expression-2%" is "spectator" 
        set field 0 of {_packet} to new a(3) # reason
        send packet {_packet} to expression-1 without calling event

effect change client weather of %players% to level %number%:
    trigger:
        set {_packet} to new play_server_game_state_change packet
        set field 0 of {_packet} to new a(7) # reason
        set field 1 of {_packet} to expression-2
        send packet {_packet} to expression-1 without calling event
```

```applescript
effect change client side equipment %string% of %entity% to %itemstack% for %players%:
    trigger:
        set {_slot} to expression-1 in upper case
        {_slot} is "CHEST" or "FEET" or "HEAD" or "LEGS" or "MAINHAND" or "OFFHAND"
        set {_entity} to expression-2
        {_entity} is an entity
        set {_item} to expression-3 ? air
        set {_nmsItem} to nms of (random item out of {_item})
        {_nmsItem} is set
        set {_players::*} to expression-4
        set {_packet} to new play_server_entity_equipment packet
        set field 0 of {_packet} to id of {_entity}
        set {_itemSlot} to enum {_slot} from nms class "EnumItemSlot"
        set {_pair} to pair {_itemSlot} with {_nmsItem}
        set {_data} to {_pair} as arraylist
        set field 1 of {_packet} to {_data}
        send {_players::*} packet {_packet}
```

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
        set field 5 of {_packet} to {_byte::*} as primitive byte Array
        set field 6 of {_packet} to {_MapChunk}.g # Represent all Tiles Entities
        set field 7 of {_packet} to true # Represent a full chunk, biomes are store only if true
        set field 8 of {_packet} to true
        set field 9 of {_packet} to {_} as arraylist # Empty ArrayList
        send packet {_packet} to expression-3 without calling event
        
command /biome [<biome>] [<int>]:
    permission: fakebiome.cmd
    trigger:
        delete {biome}
        if arg-1 is set:
            set {biome} to arg-1
            BiomeStorage({biome})
        send "Fake biome set to %{biome}%" to sender

on packet event play_server_map_chunk:
    {biome} is set
    field 7 of event-packet is true # Represent a full chunk, biomes are store only if true
    set field 4 of event-packet to BiomeStorage({biome})
    #cancel event
    #send packet event-packet to event-player without calling event
```

```applescript
effect change client side gamemode of %players% to %gamemode%:
    trigger:
        set {_packet} to new play_server_game_state_change packet
        set field 1 of {_packet} to 0 if "%expression-2%" is "survival"
        set field 1 of {_packet} to 1 if "%expression-2%" is "creative"
        set field 1 of {_packet} to 2 if "%expression-2%" is "adventure"
        set field 1 of {_packet} to 3 if "%expression-2%" is "spectator" 
        set field 0 of {_packet} to new a(3) # reason
        send packet {_packet} to expression-1 without calling event
```

```applescript
effect change client weather of %players% to level %number%:
    trigger:
        set {_packet} to new play_server_game_state_change packet
        set field 0 of {_packet} to new a(7) # reason
        set field 1 of {_packet} to expression-2
        send packet {_packet} to expression-1 without calling event
```
