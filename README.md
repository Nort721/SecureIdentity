# SecureIdentity
A player identity verification plugin.

This plugin was made a long time ago, the reason it was made is - I was volunteering as a software developer at a medium-sized Minecraft server when they were attacked by a player that used a UUID spoofing exploit
to join the server impersonating an account that had strong permissions.

## Features
* Anti Alt - detect track and prevent players from using alt accounts to ban evade
* Anti UUID Spoofing - confirm whether the UUID of the player corresponds to their name to detect and prevent UUID spoofing exploits
* Password protect accounts - you can set password protection to specific accounts as a second-factor authentication measure, this is recommended to protect accounts that have strong permissions on the server.
The passwords are salted using a static and dynamic salt and hashed in multiple layers (amount of layers is configurable)

#### No Mojang API Overuse
SecureIdentity will store the player-verified UUID in a file so it doesn't need to contact Mojang servers every time,
and therefore not top the limit of Mojang API requests the server can send.

### Note
This plugin was made by me a long time ago so the code is not the best it can be, however, if you decide to use it make sure to at least change the static salt constant in the cryptographic utils class, also note that there is way more that needs to be done
to store the passwords in a secure manner like using PBKDF2 properly or using an Authenticator app like Google Authenticator.
