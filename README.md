Builds: http://www.mediafire.com/tcooc

### Usage ###
Place jar file into the *coremods* folder.

1. Run minecraft to generate default configs.
2. Edit configs to enable and customize modules.

## Customizing modules/Editing configs ##
### Enable: ###
1. Look for *modules* section.
2. Enable modules you want (by setting them to true). Modules have comments above them explaining them.

### Customize: ###
1. Look in appropriate section (same name as the module you want to edit).
2. Edit settings (true/false, number, name, etc.). Settings have comments above them explaining them.


## Irc Module ##
/irc [command] [parameters]

### Commands: ###
connect <server, default=irc.esper.net> <port, default=6667> - connects to irc server

disconnect - disconnects from server

pong - toggle autopongs

prefix [prefix... - set prefix

mode [mode] - cycles through irc modes, or sets it to [mode] if given

### Modes ###
Default - minecraft chat

Direct - direct send to server

Prefix - same as direct except prefends the prefix

### Example Usage ###
/irc pong (enable autopong, if youre a noob)

/irc connect (connect to irc.esper.net:6667)

/irc mode (change to Direct mode)

USER yourname * * *

NICK yourname

JOIN #channel

/irc prefix PRIVMSG #channel : (set your prefix to send chat to channel)

/irc mode (Prefix mode)

Hello! (say hi)
