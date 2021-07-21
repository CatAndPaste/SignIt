# SignMap
Minecraft plugin which gives your players an ability to sign maps preventing other players from copying them.

<div style="text-align:center><img src="https://i.imgur.com/NesIUH2.png" alt="Sign This" width="500" height="125"/></div>

## Commands
| Command | Aliases | Description |
| --- | --- | --- |
| signmap | sign | Sings map in main hand, preventing others from copying it |
| unsignmap | unsign | Unsigns map in main hand, so anyone can copy it. <br>Available only for player who signed the map and operators |
| signall | - | Sings all accessible maps in player's inventory |
| unsignall | - | Unsigns all accessible maps in player's inventory |
| reload | - | Reloads plugin configuration and active language file |

## Permissions
| Permission | Default | Description |
| --- | --- | --- |
| signmap.sign | true | Players with this permission are able to sign maps |
| signmap.unsign | true | Players with this permission are able to unsign maps signed by them |
| signmap.signall | true | Players with this permission are able to sign all maps in their inventory |
| signmap.unsignall | true | Players with this permission are able to unsign all allowed maps in their inventory |
| signmap.unsign.others | OP | Players with this permission are able to unsign maps signed by other players |
| signmap.reload | OP | Players with this permission are able to reload plugin configuration and active language file |

### Translations
[chinese.yml](https://github.com/CatAndPaste/SignMap/blob/main/src/main/resources/languages/chinese.yml)
[english.yml](https://github.com/CatAndPaste/SignMap/blob/main/src/main/resources/languages/english.yml)
[russian.yml](https://github.com/CatAndPaste/SignMap/blob/main/src/main/resources/languages/russian.yml)

### Configuration file
[config.yml](https://github.com/CatAndPaste/SignMap/blob/main/src/main/resources/config.yml)
