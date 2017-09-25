# QuarticFlowMultiplayer

In this version, the user can login to the server, and waiting for pair. When there are enough users, server will open a room for these users and give each user a pairId. All the users in the same room can see each other. We add state pattern for server, which can handle initial/login/pair state. For the client, we add several listeners for network state, and use delegate for the listeners of client data changed.

##New Feature: 
 - Allow the user to reconnection without reopen Unity 
 - Client Network Connection Process:
     StartConnection(remoteIp) -> Login(id) -> StopConnection()
 

