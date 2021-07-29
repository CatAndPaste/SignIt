# SignIt
Simple Minecraft plugin that gives your players an ability to sign maps preventing others from copying those.

<div style="text-align:center"><a href="#"><img src="https://i.imgur.com/kmCeEV2.png" width="500" height="125" /></a></div>

This plugin was made with an idea to help creators on your server which make pixel art maps or drawings to protect their work.
They can use /signit (/sign or any other alias you set in the config!) command to sign the map in a main hand or /signall command to sign all maps in their inventory. Since then, signed map can only be copied by its signer in both crafting grid and Cartography table. If the player tries to copy the map signed by someone else, they'll be shown a message. Sign can be removed using /unsignit (/unsign) or /unsignall commands only by signer or player with specific permission (OP by default), so anyone can copy or sign the map again.

<img src="https://bstats.org/signatures/bukkit/SignIt.svg" />

### Translations
[english.yml](https://github.com/CatAndPaste/SignIt/blob/main/src/main/resources/languages/english.yml), [chinese.yml](https://github.com/CatAndPaste/SignIt/blob/main/src/main/resources/languages/chinese.yml) made by 小龍, [russian.yml](https://github.com/CatAndPaste/SignIt/blob/main/src/main/resources/languages/russian.yml)

### Configuration file
[config.yml](https://github.com/CatAndPaste/SignIt/blob/main/src/main/resources/config.yml)

### TO DO:
- Banners support
- Written books support

## Commands
| Command | Aliases | Description |
| --- | --- | --- |
| signit | sign | Sings map in main hand, preventing others from copying it |
| unsignit | unsign | Unsigns map in main hand, so anyone can copy it. <br>Available only for player who signed the map and operators |
| signall | - | Sings all accessible maps in player's inventory |
| unsignall | - | Unsigns all accessible maps in player's inventory |
| signit reload | - | Reloads plugin configuration and language file specified in config |

## Permissions
| Permission | Default | Description |
| --- | --- | --- |
| signit.sign | true | Players with this permission are able to sign maps |
| signit.unsign | true | Players with this permission are able to unsign maps signed by them |
| signit.signall | true | Players with this permission are able to sign all maps in their inventory |
| signit.unsignall | true | Players with this permission are able to unsign all allowed maps in their inventory |
| signit.unsign.others | OP | Players with this permission are able to unsign maps signed by other players |
| signit.reload | OP | Players with this permission are able to reload plugin configuration and active language file |
