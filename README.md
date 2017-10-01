# QuarticFlowMultiplayer

In this version, the user can login to the server, and waiting for pair. When there are enough users, server will open a room for these users and give each user a pairId. All the users in the same room can see each other. We add state pattern for server, which can handle initial/login/pair state. For the client, we add several listeners for network state, and use delegate for the listeners of client data changed.

## New Feature: 
 - Allow the user to reconnection without reopen Unity 
 - Client Network Connection Process:
     StartConnection(remoteIp) -> Login(id) -> StopConnection()
 
## Server -> Client (98 Bytes):
 - byte: id
 - byte: pairId
 - int: score
 - float: breathDegree
 - float: breathHeight
 - Transform: head
 - Transform: lHand
 - Transform: rHand


## Client -> Server (96 Bytes):
 - int: score
 - float: breathDegree
 - float: breathHeight
 - Transform: head
 - Transform: lHand
 - Transform: rHand

## Request/Response Type:

[Request/Response Type] [data]

### Server -> Client:
 - NEWSTATUS [data]
 - PAIRID [data]
 - SUCCESS 
 - $ [data]

### Client -> Server:
 - ['\0']
 - $ [data]
 - CLOSE
 - STATUS [data]
 - LOGIN [data]
